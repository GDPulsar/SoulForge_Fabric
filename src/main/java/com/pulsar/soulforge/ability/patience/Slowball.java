package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.SlowballProjectile;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class Slowball extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        World world = player.getWorld();
        SlowballProjectile projectile = new SlowballProjectile(world, player);
        projectile.setPosition(player.getEyePos());
        projectile.setVelocity(player.getRotationVector().multiply(1.5f));
        world.spawnEntity(projectile);
        player.getServerWorld().playSoundFromEntity(null, player, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 1f, 1f);
        return super.cast(player);
    }

    public int getLV() { return 1; }

    public int getCost() { return 20; }

    public int getCooldown() { return 200; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Slowball();
    }
}
