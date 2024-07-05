package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;

public class PainSplit extends AbilityBase {
    public PlayerEntity target = null;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (target == null) {
            EntityHitResult hit = Utils.getFocussedEntity(player, 5);
            if (hit != null) {
                Entity entity = hit.getEntity();
                if (entity instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canHealPlayer(player.getServer(), player, targetPlayer)) return false;
                    target = targetPlayer;
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_HEAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                    return super.cast(player);
                }
            }
        } else {
            target = null;
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (target != null) {
            if (target.distanceTo(player) >= 300f) target = null;
        }
        return target == null;
    }

    public int getLV() { return 15; }

    public int getCost() { return 40; }

    public int getCooldown() { return 200; }

    public AbilityType getType() { return AbilityType.TOGGLE; }

    @Override
    public AbilityBase getInstance() {
        return new PainSplit();
    }
}
