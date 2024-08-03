package com.pulsar.soulforge.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.accessors.ValueHolder;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.WorldBaseComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class LivingDamageEvent {
    /**
     * Called whenever a LivingEntity takes damage.
     * @param living The LivingEntity being damaged.
     * @param source The DamageSource
     * @param damage The amount of damage being taken. This is unmodified by things like armor, resistance and protection.
     * @return Whether the entity should take damage. Cancels all other methods if false.
     */
    public static boolean onTakeDamage(LivingEntity living, DamageSource source, float damage) {
        ValueHolder values = (ValueHolder)living;
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
            float targetDefence;
            if (living.getAttributes().hasAttribute(EntityAttributes.GENERIC_ARMOR)) targetDefence = (float)living.getAttributeValue(EntityAttributes.GENERIC_ARMOR);
            else targetDefence = 0f;

            float targetDamage;
            if (living.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE)) targetDamage = (float)living.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            else targetDamage = 0f;

            int expIncrease = (int)(damage * (1f + (targetDefence / 10f) + (targetDamage / 10f)));

            WorldBaseComponent worldComponent = SoulForge.getWorldComponent(player.getWorld());
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
        ValueHolder values = (ValueHolder)living;
        if (values.hasInt("ChildOfOmelasTimer") && values.getInt("ChildOfOmelasTimer") > 0) {
            if (source.getAttacker() instanceof LivingEntity attacker) {
                attacker.heal(damage);
            }
        }
    }
}
