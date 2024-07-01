package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ExpandingForce extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        Box box = new Box(player.getPos().subtract(10, 10, 10), player.getPos().add(10, 10, 10));
        for (Entity target : world.getOtherEntities(player, box)) {
            if (target.distanceTo(player) < 4f) {
                if (target instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) return false;
                }
                Vec3d diff = target.getPos().subtract(player.getPos());
                float dist = (float) diff.length();
                Vec3d push = diff.normalize().multiply(2.5f * Math.log(-dist + 5));
                target.addVelocityInternal(push);
                target.velocityModified = true;
            }
        }
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_REFLECT_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        return true;
    }

    public int getLV() { return 12; }
    public int getCost() { return 25; }
    public int getCooldown() { return 200; }
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new ExpandingForce();
    }
}
