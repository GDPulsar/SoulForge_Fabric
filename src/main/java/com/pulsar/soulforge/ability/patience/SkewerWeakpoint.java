package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.FrozenEnergyProjectile;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SkewerWeakpoint extends AbilityBase {
    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        timer = 13;
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer--;
        if (timer % 3 == 0) {
            World world = player.getWorld();
            FrozenEnergyProjectile projectile = new FrozenEnergyProjectile(world, player);
            projectile.setPosition(player.getEyePos());
            float f = (float)((player.getPitch()+Math.random()*2.5f-1.25f) * 0.017453292F);
            float g = (float)(-(player.getYaw()+Math.random()*2.5f-1.25f) * 0.017453292F);
            float h = MathHelper.cos(g);
            float i = MathHelper.sin(g);
            float j = MathHelper.cos(f);
            float k = MathHelper.sin(f);
            Vec3d velocity = new Vec3d(i * j, -k, h * j);
            projectile.setVelocity(velocity.multiply(2));
            world.spawnEntity(projectile);
        }
        return timer <= 0;
    }

    public int getLV() { return 15; }

    public int getCost() { return 30; }

    public int getCooldown() { return 200; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new SkewerWeakpoint();
    }
}
