package com.pulsar.soulforge.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.List;

public class IntegrityPlatformEntity extends Entity {
    private static final TrackedData<Integer> TIME_ALIVE = DataTracker.registerData(IntegrityPlatformEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> STACK = DataTracker.registerData(IntegrityPlatformEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public IntegrityPlatformEntity(World world, Vec3d position) {
        super(SoulForgeEntities.INTEGRITY_PLATFORM_ENTITY_TYPE, world);
        this.setPosition(position);
        this.ignoreCameraFrustum = true;
        Vec3d negCorner = this.getPos().add(-1.75f, -0.25f, -1.75f);
        Vec3d posCorner = this.getPos().add(1.75f, 0f, 1.75f);
        setBoundingBox(new Box(negCorner.x, negCorner.y, negCorner.z, posCorner.x, posCorner.y, posCorner.z));
    }

    public IntegrityPlatformEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
        setStack(0);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(TIME_ALIVE, 0);
        this.dataTracker.startTracking(STACK, 0);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        setTimeAlive(nbt.getInt("time"));
        setStack(nbt.getInt("stack"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("time", getTimeAlive());
        nbt.putInt("stack", getStack());
    }

    public int getTimeAlive() {
        return this.dataTracker.get(TIME_ALIVE);
    }

    public void setTimeAlive(int value) {
        this.dataTracker.set(TIME_ALIVE, value);
    }

    public int getStack() {
        return this.dataTracker.get(STACK);
    }

    public void setStack(int value) {
        this.dataTracker.set(STACK, value);
        Vec3d negCorner = this.getPos().add(-1.75f-0.25f*value, -0.25f, -1.75f-0.25f*value);
        Vec3d posCorner = this.getPos().add(1.75f+0.25f*value, 0f, 1.75f+0.25f*value);
        setBoundingBox(new Box(negCorner.x, negCorner.y, negCorner.z, posCorner.x, posCorner.y, posCorner.z));
    }

    @Override
    public boolean collidesWith(Entity other) {
        return true;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.changing(3.5f+0.5f*getStack(), 0.25f);
    }

    @Override
    public void tick() {
        setTimeAlive(getTimeAlive()+1);
        if (getTimeAlive() >= 200 && getStack() == 0) kill();
        if (getTimeAlive() >= 100 && getStack() == 1) kill();
        if (getTimeAlive() >= 50 && getStack() == 2) kill();
        super.tick();
    }
}
