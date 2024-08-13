package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.IceSpikeProjectile;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class IceSpike extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        World world = player.getWorld();
        Vec3d lookPos;
        HitResult hit = player.raycast(20f, 0f, true);
        if (hit != null) lookPos = hit.getPos();
        else lookPos = new Vec3d(player.getRotationVector().x, 0f, player.getRotationVector().z).normalize().multiply(20f).add(player.getPos());
        BlockHitResult target = player.getWorld().raycast(new RaycastContext(lookPos, lookPos.subtract(0, 100, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
        if (target != null) {
            IceSpikeProjectile projectile = new IceSpikeProjectile(world, player);
            projectile.setPosition(target.getPos());
            projectile.setYaw(player.getYaw());
            world.spawnEntity(projectile);
            player.getServerWorld().playSoundFromEntity(null, player, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1f, 1f);
            return super.cast(player);
        }
        return false;
    }

    public int getLV() { return 5; }

    public int getCost() { return 30; }

    public int getCooldown() { return 400; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new IceSpike();
    }
}
