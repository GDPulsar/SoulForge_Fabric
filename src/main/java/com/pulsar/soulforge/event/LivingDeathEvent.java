package com.pulsar.soulforge.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

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
            soulData.setEXP(soulData.getEXP() + Utils.getKillExp(living, player));
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
