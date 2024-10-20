package com.pulsar.soulforge.ability.other;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.SkullProjectile;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;

public class BadToTheBone extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult target = Utils.getFocussedEntity(player, 100f, entity -> entity instanceof LivingEntity && !entity.isInvisible());
        if (target != null) {
            if (target.getEntity() instanceof LivingEntity living) {
                ServerWorld world = player.getServerWorld();
                world.playSoundAtBlockCenter(player.getBlockPos(), SoulForgeSounds.SKULL_EVENT, SoundCategory.MASTER, 5f, 1f, false);
                SkullProjectile projectile = new SkullProjectile(world, living);
                projectile.setPosition(player.getEyePos());
                world.spawnEntity(projectile);
                projectile.setOwner(living);
                return super.cast(player);
            }
        }
        return false;
    }

    public int getLV() { return 1; }

    public int getCost() { return 100; }

    public int getCooldown() { return 1200; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new BadToTheBone();
    }
}
