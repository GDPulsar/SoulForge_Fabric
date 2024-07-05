package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.DeterminationShotProjectile;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class DeterminationShot extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        World world = player.getWorld();
        DeterminationShotProjectile projectile = new DeterminationShotProjectile(world, player);
        projectile.setPosition(player.getEyePos());
        projectile.setVelocity(player.getRotationVector().multiply(4f));
        world.spawnEntity(projectile);
        player.getServerWorld().playSoundFromEntity(null, player, SoulForgeSounds.PELLET_SUMMON_EVENT, SoundCategory.PLAYERS, 1f, 0.75f);
        return super.cast(player);
    }

    public int getLV() { return 5; }

    public int getCost() { return 10; }

    public int getCooldown() { return 20; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new DeterminationShot();
    }
}
