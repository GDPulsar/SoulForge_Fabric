package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FearBombEntity extends Entity {
    private static final TrackedData<Boolean> DETONATING = DataTracker.registerData(FearBombEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> TIMER = DataTracker.registerData(FearBombEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(FearBombEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public PlayerEntity owner;

    public FearBombEntity(World world, PlayerEntity owner) {
        this(SoulForgeEntities.FEAR_BOMB_ENTITY_TYPE, world);
        this.dataTracker.set(OWNER_UUID, Optional.of(owner.getUuid()));
        this.owner = owner;
    }

    public FearBombEntity(EntityType<FearBombEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(DETONATING, false);
        this.dataTracker.startTracking(TIMER, 0);
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    public boolean getDetonating() { return this.dataTracker.get(DETONATING); }

    public PlayerEntity getOwner() {
        Optional<UUID> uuid = this.dataTracker.get(OWNER_UUID);
        return uuid.map(value -> this.getWorld().getPlayerByUuid(value)).orElse(null);
    }
    public void setDetonatingTimer(int time) {
        this.dataTracker.set(TIMER, time);
    }

    public void setDetonating(boolean detonating) { this.dataTracker.set(DETONATING, detonating); }

    @Override
    public void tick() {
        if (owner == null && this.dataTracker.get(OWNER_UUID).isPresent()) {
            this.owner = this.getWorld().getPlayerByUuid(this.dataTracker.get(OWNER_UUID).get());
        } else if (this.dataTracker.get(OWNER_UUID).isPresent() && owner != null && (this.dataTracker.get(OWNER_UUID).get() != owner.getUuid())) {
            this.owner = this.getWorld().getPlayerByUuid(this.dataTracker.get(OWNER_UUID).get());
        }
        if (owner != null) {
            if (getDetonating()) {
                if (!getWorld().isClient) this.dataTracker.set(TIMER, this.dataTracker.get(TIMER)-1);
                if (this.dataTracker.get(TIMER) <= 0) {
                    List<Entity> list = getWorld().getOtherEntities(this, Box.of(getPos(), 8, 8,8));
                    for (Entity entity : list) {
                        entity.timeUntilRegen = 0;
                    }
                    this.getWorld().createExplosion(this, getBlockX(), getBlockY(), getBlockZ(), 3f, World.ExplosionSourceType.TNT);
                    this.kill();
                }
                if (this.dataTracker.get(TIMER) == 10) {
                    this.getWorld().playSound(null, this.getBlockPos(), SoulForgeSounds.MINE_BEEP_EVENT, SoundCategory.MASTER, 1f, 1f);
                }
            }
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
