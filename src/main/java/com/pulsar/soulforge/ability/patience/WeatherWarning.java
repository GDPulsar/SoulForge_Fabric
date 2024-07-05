package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.HailProjectile;
import com.pulsar.soulforge.entity.WeatherWarningLightningEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class WeatherWarning extends AbilityBase {
    public int timer = 0;
    public int stage = 0;
    public BlockPos origin = null;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        origin = player.getBlockPos();
        timer = 200;
        stage = 0;
        if (player.getWorld().isRaining()) stage = 1;
        if (player.getWorld().isThundering()) stage = 2;
        player.getServerWorld().setWeather(0, 6000, true, stage >= 1);
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer--;
        World world = player.getWorld();
        if (stage >= 0) {
            HailProjectile projectile = new HailProjectile(world, player, true);
            projectile.setPosition(origin.toCenterPos().add(Math.random() * 75f - 27.5f, 70f, Math.random() * 75 - 42.5f));
            Vec3d velocity = new Vec3d(-0.25f, -2f, 0.5f);
            projectile.setVelocity(velocity);
            world.spawnEntity(projectile);
        }
        if (stage >= 1) {
            if (timer % 2 == 0) {
                HailProjectile projectile = new HailProjectile(world, player, true);
                projectile.setPosition(origin.toCenterPos().add(Math.random() * 75f - 27.5f, 70f, Math.random() * 75 - 42.5f));
                Vec3d velocity = new Vec3d(-0.25f, -2f, 0.5f);
                projectile.setVelocity(velocity);
                world.spawnEntity(projectile);
            }
        }
        if (stage >= 2) {
            if (timer % 15 == 0) {
                Vec3d position = new Vec3d((Math.random() * 60 - 30) + origin.getX(), 320f, ((Math.random() * 60 - 30) + origin.getZ()));
                BlockHitResult top = world.raycast(new RaycastContext(position, position.subtract(0f, 300f, 0f), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.SOURCE_ONLY, player));
                WeatherWarningLightningEntity lightning = new WeatherWarningLightningEntity(player);
                lightning.setPosition(top.getBlockPos().toCenterPos());
                world.spawnEntity(lightning);
            }
        }
        return timer <= 0;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        stage = 0;
        return super.end(player);
    }

    public int getLV() { return 17; }

    public int getCost() { return 40; }

    public int getCooldown() { return 600; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new WeatherWarning();
    }
}
