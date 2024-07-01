package com.pulsar.soulforge.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
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
            Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "antigravity_zone");
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            playerSoul.removeTag("antigravityZoneAffected");
        }
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);

            // antigravity zones
            if (playerSoul.hasCast("Antigravity Zone")) {
                for (ServerPlayerEntity target : server.getPlayerManager().getPlayerList()) {
                    SoulComponent targetSoul = SoulForge.getPlayerSoul(target);
                    float lowestModifier = 0f;
                    if (target.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).hasModifier(Identifier.of(SoulForge.MOD_ID, "antigravity_zone"))) {
                        EntityAttributeModifier modifier = target.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).getModifier(Identifier.of(SoulForge.MOD_ID, "antigravity_zone"));
                        lowestModifier = (float)modifier.value();
                    }
                    float slowAmount = -0.02f * playerSoul.getEffectiveLV();
                    if (lowestModifier > slowAmount) {
                        Utils.removeModifier(target, EntityAttributes.GENERIC_MOVEMENT_SPEED, "antigravityZone");
                        if (target.distanceTo(player) < 15f) {
                            EntityAttributeModifier antigravityZoneModifer = new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "antigravity_zone"), slowAmount, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                            if (target != player) target.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(antigravityZoneModifer);
                            targetSoul.addTag("antigravityZoneAffected");
                        }
                    }
                }
            }

            // ultrakill sliding
            if (player.isSneaking() && wasSneaking.containsKey(player)) {
                if (!wasSneaking.get(player)) {
                    if (playerSoul.hasCast("Accelerated Pellet Aura")) {
                        if (player.isOnGround()) {
                            if (player.isSprinting()) {
                                playerSoul.addTag("sliding");
                                playerSoul.setValue("slideX", (float)player.getRotationVector().withAxis(Direction.Axis.Y, 0).normalize().x * 0.75f);
                                playerSoul.setValue("slideZ", (float)player.getRotationVector().withAxis(Direction.Axis.Y, 0).normalize().z * 0.75f);
                            }
                        } else {
                            if (!playerSoul.hasCast("Platforms") && !playerSoul.hasCast("Determination Platforms")) {
                                player.setVelocity(0, -2, 0);
                                player.velocityModified = true;
                                playerSoul.addTag("groundSlam");
                            }
                        }
                    }

                }
            }
            wasSneaking.put(player, player.isSneaking());

            if (playerSoul.hasCast("Accelerated Pellet Aura")) {
                if (playerSoul.hasTag("sliding") && playerSoul.hasValue("slideX") && playerSoul.hasValue("slideZ")) {
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
                        playerSoul.removeTag("sliding");
                    } else {
                        player.setVelocity(new Vec3d(playerSoul.getValue("slideX"), falling ? player.getVelocity().y : 0, playerSoul.getValue("slideZ")));
                        player.velocityModified = true;
                    }
                    /*Vec3d movement = player.getVelocity();
                    List<VoxelShape> list = player.getWorld().getEntityCollisions(player, player.getBoundingBox().stretch(movement));
                    Vec3d a = movement.lengthSquared() == 0.0 ? movement : Entity.adjustMovementForCollisions(player, movement, player.getBoundingBox(), player.getWorld(), list);
                    Vec3d b = Entity.adjustMovementForCollisions(player, new Vec3d(movement.x, 1, movement.z),
                            player.getBoundingBox(), player.getWorld(), list);
                    Vec3d c = Entity.adjustMovementForCollisions(player, new Vec3d(0.0, 1, 0.0),
                            player.getBoundingBox().stretch(movement.x, 0.0, movement.z), player.getWorld(), list);
                    if (c.y < 1) {
                        Vec3d adjusted = Entity.adjustMovementForCollisions(player, new Vec3d(movement.x, 0.0, movement.z),
                                player.getBoundingBox().offset(c), player.getWorld(), list).add(c);
                        if (adjusted.horizontalLengthSquared() > b.horizontalLengthSquared()) {
                            b = adjusted;
                        }
                    }

                    if (b.horizontalLengthSquared() > a.horizontalLengthSquared()) {
                        player.setPosition(b.add(Entity.adjustMovementForCollisions(player, new Vec3d(0.0, -b.y + movement.y, 0.0), player.getBoundingBox().offset(b), player.getWorld(), list)));
                    }*/
                }
            }
        }
    }
}
