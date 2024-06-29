package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ImmobilizationEntity extends Entity implements Attackable {
    public float maxHealth;
    public float health;
    private LivingEntity target;
    private static final TrackedData<Vector3f> SIZE = DataTracker.registerData(ImmobilizationEntity.class, TrackedDataHandlerRegistry.VECTOR3F);

    public ImmobilizationEntity(World world, Vec3d position, float health, LivingEntity target) {
        super(SoulForgeEntities.IMMOBILIZATION_ENTITY_TYPE, world);
        this.setPosition(position);
        this.maxHealth = health;
        this.health = health;
        this.setEntity(target);
        this.setSize((float)Math.max(target.getBoundingBox().getXLength(), target.getBoundingBox().getZLength()), (float)target.getBoundingBox().getYLength());
        this.calculateDimensions();
        setBoundingBox(Box.of(this.getPos(), this.getSizeX(), this.getSizeY(), this.getSizeX()));
    }

    public ImmobilizationEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
        maxHealth = 100f;
        health = 100f;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(SIZE, new Vector3f(1f, 2f, 1f));
    }

    @Override
    public void tick() {
        if (target != null) target.teleport(this.getX(), this.getY(), this.getZ());
        super.tick();
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        maxHealth = nbt.getFloat("maxHealth");
        health = nbt.getFloat("health");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("maxHealth", maxHealth);
        nbt.putFloat("health", health);
    }

    public float getSizeX() {
        return this.dataTracker.get(SIZE).x;
    }

    public float getSizeY() {
        return this.dataTracker.get(SIZE).y;
    }

    public void setSize(float x, float y) {
        this.dataTracker.set(SIZE, new Vector3f(x, y, x));
    }

    public void setEntity(LivingEntity entity) {
        this.target = entity;
    }

    @Nullable
    @Override
    public LivingEntity getLastAttacker() {
        return null;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        health -= amount;
        if (health <= 0) kill();
        return true;
    }

    @Override
    public boolean collidesWith(Entity other) { return true; }

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
        return EntityDimensions.fixed(getSizeX(), getSizeY());
    }
}
