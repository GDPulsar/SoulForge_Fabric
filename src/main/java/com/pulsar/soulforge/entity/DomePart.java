package com.pulsar.soulforge.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class DomePart extends Entity {
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(DomePart.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Boolean> DETERMINATION = DataTracker.registerData(DomePart.class, TrackedDataHandlerRegistry.BOOLEAN);
    public DomeEntity owner;

    public boolean canUsePortals() {
        return false;
    }

    public DomePart(DomeEntity owner, int x, int y, int z) {
        super(SoulForgeEntities.DOME_PART_TYPE, owner.getWorld());
        this.owner = owner;
        this.dataTracker.set(OWNER_UUID, Optional.of(this.owner.getUuid()));
        this.setPosition(x, y, z);
        this.dataTracker.set(DETERMINATION, false);
    }
    public DomePart(DomeEntity owner, int x, int y, int z, boolean determination) {
        this(owner, x, y, z);
        this.dataTracker.set(DETERMINATION, determination);
        this.dataTracker.set(OWNER_UUID, Optional.of(this.owner.getUuid()));
    }

    public DomePart(EntityType<DomePart> domePartEntityType, World world) {
        super(domePartEntityType, world);
    }

    public boolean isDetermination() {
        return this.dataTracker.get(DETERMINATION);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source) || this.owner == null) {
            return false;
        }
        return this.owner.damage(source, amount);
    }

    @Override
    public void tick() {
        if (this.dataTracker.get(OWNER_UUID).isPresent()) {
            for (DomeEntity domeEntity : this.getEntityWorld().getEntitiesByClass(DomeEntity.class, Box.of(this.getPos(), 200, 200, 200), (domeEntity -> domeEntity.getUuid().compareTo(this.dataTracker.get(OWNER_UUID).get()) == 0))) {
                this.owner = domeEntity;
                break;
            }
        }
        if (this.owner != null) {
            if (this.owner.isRemoved()) {
                this.kill();
            }
        }
        super.tick();
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
        this.dataTracker.startTracking(DETERMINATION, false);
    }
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}
    @Override
    public boolean canHit() { return true; }
    @Override
    public boolean isFireImmune() { return true; }
    @Override
    public boolean isPartOf(Entity entity) { return this == entity || this.owner == entity; }
    @Override
    public EntityDimensions getDimensions(EntityPose pose) { return EntityDimensions.fixed(1.1f, 1.1f); }

    @Override
    protected Box calculateBoundingBox() {
        return super.calculateBoundingBox().offset(0.5f, -0.05f, 0.5f);
    }
    @Override
    public boolean shouldSave() { return false; }
    @Override
    public boolean shouldRender(double distance) { return true; }
    @Override
    public boolean collidesWith(Entity other) {
        return true;
    }
    @Override
    public boolean isCollidable() {
        return true;
    }
}
