package com.pulsar.soulforge.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlayerEvents {
    /**
     * Called whenever a player attacks a LivingEntity.
     * @param player The Player attacker.
     * @param target The LivingEntity being attacked.
     * @param source The DamageSource
     * @param damage The amount of damage being taken. This is unmodified by things like armor, resistance and protection.
     * @param actuallyDamaged If the target actually took damage.
     */
    public static void onAttackEntity(PlayerEntity player, Entity target, DamageSource source, float damage, boolean actuallyDamaged) {
        if (target instanceof LivingEntity living) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            siphon:
            {
                if (living instanceof PlayerEntity targetPlayer) {
                    if (!player.shouldDamagePlayer(targetPlayer)) break siphon;
                }
                if (living.blockedByShield(source)) break siphon;
                if (living.isInvulnerableTo(source)) break siphon;

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
                                playerSoul.addMagic(damage);
                            }
                            if (type == Siphon.Type.PERSEVERANCE || type == Siphon.Type.SPITE) {
                                if (player.getAttackCooldownProgress(0.5f) >= 0.99f) {
                                    Utils.addAntiheal(0.6f, (int) (player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED).getValue() * 20), living);
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
                                playerSoul.addMagic(damage);
                            }
                        }
                    }
                }
            }
        }
    }
}
