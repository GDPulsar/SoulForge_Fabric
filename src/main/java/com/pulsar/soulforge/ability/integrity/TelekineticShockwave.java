package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class TelekineticShockwave extends AbilityBase {
    public int gojoTimer = 0;
    public boolean isGojo = false;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        isGojo = playerSoul.getTraits().contains(Traits.bravery) && playerSoul.getTraits().contains(Traits.integrity);
        if (!isGojo) {
            ServerWorld world = player.getServer().getWorld(player.getWorld().getRegistryKey());
            if (world != null) {
                Box box = new Box(player.getPos().subtract(10, 10, 10), player.getPos().add(10, 10, 10));
                for (Entity target : world.getOtherEntities(player, box)) {
                    if (target.distanceTo(player) < 4f) {
                        Vec3d diff = target.getPos().subtract(player.getPos());
                        float dist = (float) diff.length();
                        Vec3d push = diff.normalize().multiply(playerSoul.getEffectiveLV()*0.15f * Math.log(-dist + 5)).add(0, 1f, 0);
                        if (target.distanceTo(player) <= 2f) push.multiply(0.5f, 1f, 0.5f);
                        else push.multiply(1f, 0.5f, 1f);
                        target.addVelocity(push);
                        target.velocityModified = true;
                    }
                }
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_REFLECT_EVENT, SoundCategory.PLAYERS, 1f, 1f);
            }
        } else {
            gojoTimer = 0;
        }
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (isGojo) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            ServerWorld world = player.getServer().getWorld(player.getWorld().getRegistryKey());
            if (world != null) {
                if (gojoTimer % 2 == 0) {
                    Box box = new Box(player.getPos().subtract(10, 10, 10), player.getPos().add(10, 10, 10));
                    for (Entity target : world.getOtherEntities(player, box)) {
                        if (target.distanceTo(player) < 4f) {
                            Vec3d diff = target.getPos().subtract(player.getPos()).normalize().multiply(1.5f);
                            Vec3d push = diff.multiply(2f);
                            target.setVelocity(push);
                            target.velocityModified = true;
                            RaycastContext raycastContext = new RaycastContext(target.getPos(), target.getPos().add(target.getVelocity()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, target);
                            HitResult hit = world.raycast(raycastContext);
                            if (hit != null) {
                                target.damage(player.getDamageSources().playerAttack(player), 3f);
                                target.timeUntilRegen = 0;
                                target.setPosition(hit.getPos());
                                target.setVelocity(Vec3d.ZERO);
                            }
                        }
                    }
                }
                if (gojoTimer % 10 == 0) player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.FROST_WAVE_EVENT, SoundCategory.PLAYERS, 5f, 1f);
            }
            gojoTimer++;
            return gojoTimer >= playerSoul.getEffectiveLV()*3f;
        }
        return super.tick(player);
    }

    public int getLV() { return 15; }

    public int getCost() { return 35; }

    public int getCooldown() { return 200; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new TelekineticShockwave();
    }
}
