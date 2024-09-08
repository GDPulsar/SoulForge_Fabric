package com.pulsar.soulforge.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.ValueComponent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.HashMap;

public class ServerEndTick implements ServerTickEvents.EndTick {
    private final HashMap<ServerPlayerEntity, Boolean> wasSneaking = new HashMap<>();

    @Override
    public void onEndTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            ValueComponent values = SoulForge.getValues(player);

            // ultrakill sliding
            if (player.isSneaking() && wasSneaking.containsKey(player)) {
                if (!wasSneaking.get(player)) {
                    if (playerSoul.hasCast("Accelerated Pellet Aura")) {
                        if (player.isOnGround()) {
                            if (player.isSprinting()) {
                                values.setBool("sliding", true);
                                values.setFloat("slideX", (float)player.getRotationVector().withAxis(Direction.Axis.Y, 0).normalize().x * 0.75f);
                                values.setFloat("slideZ", (float)player.getRotationVector().withAxis(Direction.Axis.Y, 0).normalize().z * 0.75f);
                            }
                        } else {
                            if (!playerSoul.hasCast("Platforms") && !playerSoul.hasCast("Determination Platforms")) {
                                player.setVelocity(0, -2, 0);
                                player.velocityModified = true;
                                values.setBool("groundSlam", true);
                            }
                        }
                    }

                }
            }
            wasSneaking.put(player, player.isSneaking());

            if (playerSoul.hasCast("Accelerated Pellet Aura")) {
                if (values.getBool("sliding") && values.hasFloat("slideX") && values.hasFloat("slideZ")) {
                    HitResult hit = player.getWorld().raycast(new RaycastContext(player.getPos(), player.getPos().subtract(0, 2, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, player));
                    boolean airborne = true;
                    boolean falling = false;
                    if (hit != null && hit.getType() != HitResult.Type.MISS) {
                        float distance = (float)hit.getPos().distanceTo(player.getPos());
                        if (distance <= 1f) {
                            airborne = false;
                            if (distance > 0.1f) falling = true;
                        }
                    }
                    if (!player.isSneaking() || airborne) {
                        values.removeBool("sliding");
                    } else {
                        player.setVelocity(new Vec3d(values.getFloat("slideX"), falling ? player.getVelocity().y : 0, values.getFloat("slideZ")));
                        player.velocityModified = true;
                    }
                }
            }
        }
    }
}
