package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.EnergyBallProjectile;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class EnergyBall extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        World world = player.getWorld();
        EnergyBallProjectile projectile = new EnergyBallProjectile(world, player);
        projectile.setPosition(player.getEyePos());
        projectile.setVelocity(player.getRotationVector().multiply(2));
        world.spawnEntity(projectile);
        player.getServerWorld().playSoundFromEntity(null, player, SoulForgeSounds.PELLET_SUMMON_EVENT, SoundCategory.PLAYERS, 1f, 0.5f);
        return super.cast(player);
    }

    public int getLV() { return 1; }

    public int getCost() { return 20; }

    public int getCooldown() { return 100; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new EnergyBall();
    }
}
