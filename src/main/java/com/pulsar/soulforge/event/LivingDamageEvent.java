package com.pulsar.soulforge.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.ValueComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class LivingDamageEvent {
    /**
     * Called whenever a LivingEntity takes damage.
     * @param living The LivingEntity being damaged.
     * @param source The DamageSource
     * @param damage The amount of damage being taken. This is unmodified by things like armor, resistance and protection.
     * @return Whether the entity should take damage. Cancels all other methods if false.
     */
    public static boolean onTakeDamage(LivingEntity living, DamageSource source, float damage, boolean actuallyDamaged) {
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

            if (actuallyDamaged) {
                playerSoul.setEXP(playerSoul.getEXP() + Utils.getDamageExp(living, player, damage));

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
