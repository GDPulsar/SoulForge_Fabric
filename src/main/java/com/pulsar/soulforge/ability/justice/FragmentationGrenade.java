package com.pulsar.soulforge.ability.justice;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.FragmentationGrenadeProjectile;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class FragmentationGrenade extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        World world = player.getWorld();
        FragmentationGrenadeProjectile projectile = new FragmentationGrenadeProjectile(world, player.getEyePos(), player);
        projectile.setPosition(player.getEyePos());
        projectile.setVelocity(player.getRotationVector().multiply(1.5f));
        projectile.setOwner(player);
        world.spawnEntity(projectile);
        return true;
    }

    public int getLV() { return 17; }

    public int getCost() { return 40; }

    public int getCooldown() { return 300; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new FragmentationGrenade();
    }
}
