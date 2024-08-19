package com.pulsar.soulforge.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.WorldComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class LivingDeathEvent {
    public static void onDeath(LivingEntity living) {
        if (living instanceof TameableEntity tameable) {
            if (tameable.getOwner() instanceof PlayerEntity player) {
                if (Utils.hasHate(player)) {
                    Utils.addHate(player, 33f);
                }
            }
        }
    }

    public static void onKilledBy(LivingEntity living, LivingEntity killer) {
        if (killer instanceof ServerPlayerEntity player) {
            SoulComponent soulData = SoulForge.getPlayerSoul(player);
            float targetHealth;
            if (living.getAttributes().hasAttribute(EntityAttributes.GENERIC_MAX_HEALTH)) targetHealth = (float)living.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            else targetHealth = 0f;

            float targetDefence;
            if (living.getAttributes().hasAttribute(EntityAttributes.GENERIC_ARMOR)) targetDefence = (float)living.getAttributeValue(EntityAttributes.GENERIC_ARMOR);
            else targetDefence = 0f;

            float targetDamage;
            if (living.getAttributes().hasAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE)) targetDamage = (float)living.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            else targetDamage = 0f;

            int expIncrease;
            if (living.isPlayer()) {
                PlayerEntity targetPlayer = (PlayerEntity)living;
                SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
                expIncrease = (int)(250*(1+(targetDefence/10)*(targetSoul.getLV()/4f)));
            } else if (living.getType() == EntityType.ENDER_DRAGON) {
                expIncrease = 3000;
            } else if (living.getType() == EntityType.WITHER) {
                expIncrease = 1500;
            } else if (living.getType() == EntityType.ELDER_GUARDIAN) {
                expIncrease = 500;
            } else if (living.getType() == EntityType.EVOKER) {
                expIncrease = 250;
            } else if (living.getType() == EntityType.WARDEN) {
                expIncrease = 1000;
            } else if (living.getType() == EntityType.PIGLIN_BRUTE) {
                expIncrease = 250;
            } else {
                expIncrease = (int)(targetHealth*(1+(targetDefence/10f)+(targetDamage/10f)));
            }
            WorldComponent worldComponent = SoulForge.getWorldComponent(player.getWorld());
            expIncrease = (int)(worldComponent.getExpMultiplier() * expIncrease);
            if (living.isMobOrPlayer()) {
                if (living.isPlayer()) {
                    if (soulData.getPlayerSouls().containsKey(living.getUuidAsString())) {
                        expIncrease = (int)(MathHelper.clamp(1f-soulData.getPlayerSouls().get(living.getUuidAsString())/3f, 0f, 1f) * expIncrease);
                    }
                } else {
                    if (soulData.getMonsterSouls().containsKey(living.getType().getUntranslatedName())) {
                        expIncrease = (int)(MathHelper.clamp(1f-soulData.getMonsterSouls().get(living.getType().getUntranslatedName())/50f, 0.2f, 1f) * expIncrease);
                    }
                }
            }
            soulData.setEXP(soulData.getEXP() + expIncrease);
            if (living.isMobOrPlayer()) {
                if (living.isPlayer()) soulData.addPlayerSoul(living.getUuidAsString(), 1);
                else soulData.addMonsterSoul(living, 1);
            }

            if (Utils.hasHate(player)) {
                Utils.addHate(player, 1f);
            }
        }
    }
}
