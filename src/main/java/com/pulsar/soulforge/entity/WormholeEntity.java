package com.pulsar.soulforge.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.Set;

public class WormholeEntity extends Entity {
    private static final TrackedData<Vector3f> POSITION = DataTracker.registerData(WormholeEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    private static final TrackedData<String> WORLD = DataTracker.registerData(WormholeEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Vector3f> DIRECTION = DataTracker.registerData(WormholeEntity.class, TrackedDataHandlerRegistry.VECTOR3F);

    public WormholeEntity(World world, Vec3d position, ServerWorld targetWorld, Vec3d targetPos, Vec3d direction) {
        super(SoulForgeEntities.WORMHOLE_ENTITY_TYPE, world);
        this.setPosition(position);
        this.dataTracker.set(POSITION, targetPos.toVector3f());
        this.dataTracker.set(WORLD, targetWorld.getRegistryKey().getValue().getPath());
        this.dataTracker.set(DIRECTION, direction.toVector3f());
        this.ignoreCameraFrustum = true;
    }

    public WormholeEntity(World world, Vec3d position, ServerWorld targetWorld, Vec3d targetPos) {
        super(SoulForgeEntities.WORMHOLE_ENTITY_TYPE, world);
        this.setPosition(position);
        this.dataTracker.set(POSITION, targetPos.toVector3f());
        this.dataTracker.set(WORLD, targetWorld.getRegistryKey().getValue().getPath());
        this.dataTracker.set(DIRECTION, new Vector3f(0, 1, 0));
        this.ignoreCameraFrustum = true;
    }

    public WormholeEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
        this.ignoreCameraFrustum = true;
    }

    public Vec3d getDirection() { return new Vec3d(this.dataTracker.get(DIRECTION).x, this.dataTracker.get(DIRECTION).y, this.dataTracker.get(DIRECTION).z); }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(POSITION, new Vector3f());
        builder.add(WORLD, "");
        builder.add(DIRECTION, new Vector3f(0, 1, 0));
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    public int timer = 0;
    @Override
    public void tick() {
        ServerWorld targetWorld = null;
        if (this.getServer() != null) {
            for (ServerWorld world : this.getServer().getWorlds()) {
                if (Objects.equals(world.getRegistryKey().getValue().getPath(), this.dataTracker.get(WORLD))) {
                    targetWorld = world;
                    break;
                }
            }
        }
        if (targetWorld != null) {
            for (Entity entity : this.getWorld().getOtherEntities(this, Box.of(this.getPos(), 0.5f, 0.5f, 0.5f))) {
                float horizDist = (float) entity.getPos().withAxis(Direction.Axis.Y, 0).distanceTo(getPos().withAxis(Direction.Axis.Y, 0));
                if (horizDist <= 2f) {
                    entity.teleport(targetWorld, this.dataTracker.get(POSITION).x, this.dataTracker.get(POSITION).y, this.dataTracker.get(POSITION).z,
                            Set.of(), entity.getYaw(), entity.getPitch());
                }
            }
            if (this.timer >= 200) this.kill();
            timer++;
        }
        super.tick();
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(0.5f, 2f);
    }
}
