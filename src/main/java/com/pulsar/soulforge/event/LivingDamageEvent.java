package com.pulsar.soulforge.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.ValueComponent;
import com.pulsar.soulforge.components.WorldComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class LivingDamageEvent {
    /**
     * Called whenever a LivingEntity takes damage.
     * @param living The LivingEntity being damaged.
     * @param source The DamageSource
     * @param damage The amount of damage being taken. This is unmodified by things like armor, resistance and protection.
     * @return Whether the entity should take damage. Cancels all other methods if false.
     */
    public static boolean onTakeDamage(LivingEntity living, DamageSource source, float damage) {
        ValueComponent values = SoulForge.getValues(living);
        if (values.hasInt("HangToAThreadTimer") && values.getInt("HangToAThreadTimer") > 0
                && (!values.hasBool("HangToAThreadDamaging") || !values.getBool("HangToAThreadDamaging"))) {
            float totalDamage = 0f;
            if (values.hasFloat("HangToAThreadDamage")) totalDamage = values.getFloat("HangToAThreadDamage");
            totalDamage += damage;
            values.setFloat("HangToAThreadDamage", totalDamage);
            return false;
        }
        if (source.getAttacker() instanceof ServerPlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            siphon: {
                if (living instanceof PlayerEntity targetPlayer) {
                    if (!player.shouldDamagePlayer(targetPlayer)) break siphon;
                }

                // siphon
                ItemStack held = player.getMainHandStack();
                if (held.getNbt() != null) {
                    if (held.getNbt().contains("Siphon")) {
                        Siphon.Type type = Siphon.Type.getSiphon(held.getNbt().getString("Siphon"));
                        if (held.isIn(ItemTags.SWORDS) || held.isIn(ItemTags.AXES)) {
                            if (type == Siphon.Type.PATIENCE || type == Siphon.Type.SPITE) {
                                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
                                living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 0));
                            }
                            if (type == Siphon.Type.DETERMINATION || type == Siphon.Type.SPITE) {
                                playerSoul.setMagic(playerSoul.getMagic() + damage);
                            }
                            if (type == Siphon.Type.PERSEVERANCE || type == Siphon.Type.SPITE) {
                                if (player.getAttackCooldownProgress(0.5f) >= 0.99f) {
                                    if (living instanceof PlayerEntity targetPlayer) {
                                        SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
                                        Utils.addAntiheal(0.6f, (int) (player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED).getValue() * 20), targetSoul);
                                    }
                                }
                            }
                            if (type == Siphon.Type.KINDNESS || type == Siphon.Type.SPITE) {
                                if (player.getAbsorptionAmount() < 8f)
                                    player.setAbsorptionAmount(player.getAbsorptionAmount() + 1f);
                            }
                        }
                        if (held.isOf(Items.TRIDENT)) {
                            if (player.isUsingRiptide()) {
                                if (type == Siphon.Type.KINDNESS || type == Siphon.Type.SPITE) {
                                    living.removeStatusEffect(StatusEffects.DOLPHINS_GRACE);
                                    living.removeStatusEffect(StatusEffects.WATER_BREATHING);
                                }
                                if (type == Siphon.Type.PATIENCE || type == Siphon.Type.SPITE) {
                                    int useLevel = held.getOrCreateNbt().contains("useLevel") ? held.getOrCreateNbt().getInt("useLevel") : 1;
                                    living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 240, useLevel - 1));
                                    if (useLevel >= 2)
                                        living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 240, useLevel - 2));
                                }
                                if (type == Siphon.Type.PERSEVERANCE || type == Siphon.Type.SPITE) {
                                    int j = EnchantmentHelper.getRiptide(held);
                                    float f = player.getYaw();
                                    float g = player.getPitch();
                                    float h = -MathHelper.sin(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
                                    float k = -MathHelper.sin(g * 0.017453292F);
                                    float l = MathHelper.cos(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
                                    float m = MathHelper.sqrt(h * h + k * k + l * l);
                                    float n = 3.0F * ((1.0F + (float) j) / 4.0F);
                                    h *= n / m;
                                    k *= n / m;
                                    l *= n / m;
                                    player.addVelocity(h, k, l);
                                    player.useRiptide(20);
                                    if (player.isOnGround()) {
                                        player.move(MovementType.SELF, new Vec3d(0.0, 1.1999999284744263, 0.0));
                                    }

                                    SoundEvent soundEvent;
                                    if (j >= 3) {
                                        soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
                                    } else if (j == 2) {
                                        soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
                                    } else {
                                        soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
                                    }

                                    player.getWorld().playSoundFromEntity(null, player, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                                }
                            }
                            if (type == Siphon.Type.DETERMINATION || type == Siphon.Type.SPITE) {
                                playerSoul.setMagic(playerSoul.getMagic() + damage);
                            }
                        }
                    }
                }
            }

            float targetDefence;
            if (living.getAttributes().hasAttribute(EntityAttributes.GENERIC_ARMOR)) targetDefence = (float)living.getAttributeValue(EntityAttributes.GENERIC_ARMOR);
            else targetDefence = 0f;

            float targetDamage;
            if (living.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE)) targetDamage = (float)living.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            else targetDamage = 0f;

            int expIncrease = (int)(damage * (1f + (targetDefence / 10f) + (targetDamage / 10f)));

            WorldComponent worldComponent = SoulForge.getWorldComponent(player.getWorld());
            expIncrease = (int)(worldComponent.getExpMultiplier() * expIncrease);
            if (living.isMobOrPlayer()) {
                if (living.isPlayer()) {
                    if (playerSoul.getPlayerSouls().containsKey(living.getUuidAsString())) {
                        expIncrease = (int)(MathHelper.clamp(1f-playerSoul.getPlayerSouls().get(living.getUuidAsString())/3f, 0f, 1f) * expIncrease);
                    }
                } else {
                    if (playerSoul.getMonsterSouls().containsKey(living.getType().getUntranslatedName())) {
                        expIncrease = (int)(MathHelper.clamp(1f-playerSoul.getMonsterSouls().get(living.getType().getUntranslatedName())/50f, 0.2f, 1f) * expIncrease);
                    }
                }
            }
            playerSoul.setEXP(playerSoul.getEXP() + expIncrease);

            if (source.isOf(DamageTypes.ARROW)) {
                if (source.getSource() instanceof PersistentProjectileEntity projectile) {
                    if (projectile.inBlockState == null) {
                        float distance = living.distanceTo(source.getAttacker());
                        boolean lineOfSight = player.canSee(projectile);
                        int addedStyle = (int)(damage * (distance / 20f) * (lineOfSight ? 1f : 2f));
                        playerSoul.setStyle(playerSoul.getStyle() + addedStyle);
                    }
                }
            }
            if (source.isOf(DamageTypes.TRIDENT)) {
                if (source.getSource() instanceof TridentEntity projectile) {
                    if (projectile.inBlockState == null) {
                        float distance = living.distanceTo(source.getAttacker());
                        boolean lineOfSight = player.canSee(projectile);
                        int addedStyle = (int)(damage * (distance / 20f) * (lineOfSight ? 1f : 2f));
                        playerSoul.setStyle(playerSoul.getStyle() + addedStyle);
                    }
                }
            }
            if (source.isOf(DamageTypes.EXPLOSION)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)damage);
            }
            if (source.isOf(DamageTypes.FALLING_ANVIL)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage * 10));
            }
            if (source.isOf(DamageTypes.FALLING_STALACTITE)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage * 5));
            }
            if (source.isOf(DamageTypes.FIREWORKS)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage));
            }
            if (source.isOf(DamageTypes.LIGHTNING_BOLT)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage));
            }
            if (source.isOf(DamageTypes.PLAYER_ATTACK)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage / 2f));
            }
            if (source.isOf(DamageTypes.PLAYER_EXPLOSION)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage));
            }
            if (source.isOf(DamageTypes.THROWN)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage));
            }
            if (source.isOf(SoulForgeDamageTypes.PARRY_DAMAGE_TYPE)) {
                playerSoul.setStyle(playerSoul.getStyle() + (int)(damage * 3f));
            }
            if (living instanceof PlayerEntity targetPlayer) {
                if (source.isOf(SoulForgeDamageTypes.PAIN_SPLIT_DAMAGE_TYPE)) {
                    SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
                    targetSoul.setStyle(targetSoul.getStyle() + (int)damage);
                }
            }
        }
        return true;
    }

    public static void onApplyDamage(LivingEntity living, DamageSource source, float damage) {
        ValueComponent values = SoulForge.getValues(living);
        if (values.hasInt("ChildOfOmelasTimer") && values.getInt("ChildOfOmelasTimer") > 0) {
            if (source.getAttacker() instanceof LivingEntity attacker) {
                attacker.heal(damage);
            }
        }

        if (living instanceof ServerPlayerEntity player) {
            if (Utils.hasHate(player)) {
                Utils.addHate(player, 0.01f * damage);
            }
        }

        if (source.getAttacker() instanceof PlayerEntity player) {
            if (Utils.hasHate(player)) {
                Utils.addHate(player, 0.01f * damage);
            }
        }
    }
}
