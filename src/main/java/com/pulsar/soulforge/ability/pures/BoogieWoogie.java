package com.pulsar.soulforge.ability.pures;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public class BoogieWoogie extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult target = Utils.getFocussedEntity(player, 100f, entity -> (entity instanceof ProjectileEntity || entity instanceof LivingEntity) && !entity.isInvisible());
        if (target != null) {
            if (target.getEntity() != null) {
                Entity entity = target.getEntity();
                Vec3d targetPos = entity.getPos();
                Vec3d targetVel = entity.getVelocity();
                float targetYaw = entity.getYaw();
                float targetPitch = entity.getPitch();
                entity.teleport(player.getServerWorld(), player.getPos().x, player.getPos().y, player.getPos().z, Set.of(), player.getYaw(), player.getPitch());
                entity.setVelocity(player.getVelocity());
                entity.velocityModified = true;
                if (entity instanceof ProjectileEntity) {
                    player.teleport(player.getServerWorld(), targetPos.x, targetPos.y, targetPos.z, Set.of(), player.getYaw(), player.getPitch());
                } else {
                    player.teleport(player.getServerWorld(), targetPos.x, targetPos.y, targetPos.z, Set.of(), targetYaw, targetPitch);
                }
                player.setVelocity(targetVel);
                player.velocityModified = true;
                return super.cast(player);
            }
        }
        return false;
    }

    public int getLV() { return 10; }

    public int getCost() { return 20; }

    public int getCooldown() { return 100; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new BoogieWoogie();
    }
}
