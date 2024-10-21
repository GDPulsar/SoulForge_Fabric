package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.TemporaryModifierComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class FreezeRing extends MagicItem {
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
        if (!user.isSneaking()) {
            EntityHitResult hit = Utils.getFocussedEntity(user, playerSoul.hasCast("Furioso") ? 20 : 10);
            if (hit != null) {
                if (hit.getEntity() instanceof LivingEntity target) {
                    if (!world.isClient) {
                        if (target instanceof PlayerEntity targetPlayer) {
                            if (!TeamUtils.canDamageEntity(user.getServer(), user, targetPlayer))
                                return TypedActionResult.pass(user.getStackInHand(hand));
                        }
                        world.playSoundFromEntity(null, user, SoulForgeSounds.DR_ICESHOCK_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                        DamageSource damageSource = SoulForgeDamageTypes.of(user, SoulForgeDamageTypes.ABILITY_PIERCE_DAMAGE_TYPE);
                        if (target.damage(damageSource, 5f)) {
                            target.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.FROSTBITE, playerSoul.getEffectiveLV() * 20, 0));
                            TemporaryModifierComponent modifiers = SoulForge.getTemporaryModifiers(target);
                            modifiers.addTemporaryModifier(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(UUID.fromString("21f07aa7-02f6-4722-aa06-5717d140087a"),
                                    "freeze_ring", -playerSoul.getLV()/4f, EntityAttributeModifier.Operation.ADDITION), playerSoul.getEffectiveLV());
                            playerSoul.setStyle(playerSoul.getStyle() + (int)(5f * (1f + Utils.getTotalDebuffLevel(target) / 10f)));
                        }
                        user.getItemCooldownManager().set(this, playerSoul.hasCast("Furioso") ? 50 : 100);
                        return TypedActionResult.success(user.getStackInHand(hand));
                    } else {
                        for (int i = 0; i < 20; i++) {
                            world.addParticle(ParticleTypes.SNOWFLAKE,
                                    target.getX() + Math.random() - 0.5f, target.getY() + Math.random() * 2f - 1f, target.getZ() + Math.random() - 0.5f,
                                    0, 0, 0);
                        }
                    }
                }
            }
        } else {
            Box searchBox = user.getBoundingBox().expand(10f);
            HitResult hit = ProjectileUtil.raycast(user, user.getEyePos(), user.getEyePos().add(user.getRotationVector().multiply(10f)), searchBox, entity -> true, 0);
            if (hit == null || hit.getType() == HitResult.Type.MISS) hit = user.raycast(10f, 0f, false);
            if (hit != null && hit.getType() != HitResult.Type.MISS) {
                Vec3d pos = hit.getPos();
                if (!world.isClient) {
                    float styleIncrease = 0f;
                    for (Entity target : user.getEntityWorld().getOtherEntities(user, new Box(pos.subtract(3, 3, 3), pos.add(3, 3, 3)))) {
                        if (target instanceof LivingEntity living) {
                            if (living instanceof PlayerEntity targetPlayer) {
                                if (!TeamUtils.canDamageEntity(user.getServer(), user, targetPlayer)) continue;
                            }
                            living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.EEPY, 30*playerSoul.getLV() * (playerSoul.hasCast("Furioso") ? 2 : 1), playerSoul.getEffectiveLV()));
                            styleIncrease += 5f * (1f + Utils.getTotalDebuffLevel(living)/10f);
                        }
                    }
                    playerSoul.setStyle(playerSoul.getStyle() + (int)styleIncrease);
                    user.getItemCooldownManager().set(this, playerSoul.hasCast("Furioso") ? 300 : 600);
                } else {
                    for (int i = 0; i < 50; i++) {
                        world.addParticle(ParticleTypes.EFFECT,
                                pos.getX() + Math.random() * 6f - 3f, pos.getY() + Math.random() * 6f - 3f, pos.getZ() + Math.random() * 6f - 3f,
                                0, 0, 0);
                    }
                }
                return TypedActionResult.success(user.getStackInHand(hand));
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
