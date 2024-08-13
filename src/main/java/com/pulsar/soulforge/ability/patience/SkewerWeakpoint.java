package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.FrozenEnergyProjectile;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class SkewerWeakpoint extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        World world = player.getWorld();
        FrozenEnergyProjectile projectile = new FrozenEnergyProjectile(world, player);
        projectile.setPosition(player.getEyePos());
        projectile.setVelocity(player.getRotationVector().multiply(0.01f));
        world.spawnEntity(projectile);
        //player.getServerWorld().playSoundFromEntity(null, player, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 1f, 1f);
        return super.cast(player);
    }

    public int getLV() { return 7; }

    public int getCost() { return 30; }

    public int getCooldown() { return 600; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new SkewerWeakpoint();
    }
}
