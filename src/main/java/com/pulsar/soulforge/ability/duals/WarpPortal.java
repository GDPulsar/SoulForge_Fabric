package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.WormholeEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class WarpPortal extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        Vec3d lookPos;
        HitResult hit = player.raycast(75f, 0f, true);
        if (hit != null) lookPos = hit.getPos();
        else lookPos = player.getRotationVector().multiply(75f).add(player.getPos());
        BlockHitResult target = player.getWorld().raycast(new RaycastContext(lookPos, lookPos.subtract(0, 100, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, player));
        if (target != null) {
            Vec3d direction = player.getRotationVector().withAxis(Direction.Axis.Y, 0);
            Vec3d end = target.getBlockPos().toCenterPos().add(0, 1.5, 0);
            Vec3d start = player.getPos().add(direction.normalize().multiply(2.5f)).add(0, 1, 0);
            ServerWorld serverWorld = player.getServerWorld();
            WormholeEntity startWormhole = new WormholeEntity(serverWorld, start.x, start.y, start.z, serverWorld, end.add(direction.multiply(1.5f)), 0.25f, new Vec3d(1f, 1f, 1f));
            serverWorld.spawnEntity(startWormhole);
            WormholeEntity endWormhole = new WormholeEntity(serverWorld, end.x, end.y, end.z, serverWorld, start.add(direction.multiply(-1.5f)), 0.25f, new Vec3d(1f, 1f, 1f));
            serverWorld.spawnEntity(endWormhole);
            return super.cast(player);
        }
        return false;
    }

    public int getLV() { return 10; }

    public int getCost() { return 30; }

    public int getCooldown() { return 200; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new WarpPortal();
    }
}
