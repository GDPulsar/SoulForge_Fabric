package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class SnowgraveProjectile extends Entity implements GeoEntity {
    private static final TrackedData<Vector3f> POSITION = DataTracker.registerData(SnowgraveProjectile.class, TrackedDataHandlerRegistry.VECTOR3F);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public SnowgraveProjectile(EntityType<? extends SnowgraveProjectile> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(POSITION, new Vector3f());
    }

    public void setPos(Vec3d position) { this.dataTracker.set(POSITION, position.toVector3f()); }
    public Vec3d getPos() { return Utils.vector3fToVec3d(this.dataTracker.get(POSITION)); }

    @Override
    public void tick() {
        this.setPosition(getPos());
    }

    public boolean canUsePortals() {
        return false;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        tAnimationState.getController().setAnimation(RawAnimation.begin().then("snowgrave.main", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
