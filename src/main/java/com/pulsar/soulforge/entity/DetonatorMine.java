package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DetonatorMine extends Entity {
    private static final TrackedData<Boolean> DETONATING = DataTracker.registerData(DetonatorMine.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> TIMER = DataTracker.registerData(DetonatorMine.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Optional<UUID>> OWNER = DataTracker.registerData(DetonatorMine.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    public DetonatorMine(EntityType<?> type, World world) {
        super(type, world);
    }

    public DetonatorMine(PlayerEntity owner, BlockPos position, Direction direction) {
        super(SoulForgeEntities.DETONATOR_MINE_ENTITY_TYPE, owner.getWorld());
        this.setPosition(position.toCenterPos().add(direction.getOffsetX()*0.5001f, direction.getOffsetY()*0.5001f, direction.getOffsetZ()*0.5001f));
        this.ignoreCameraFrustum = true;
        this.setOwner(owner);
    }

    public boolean canUsePortals() {
        return false;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(DETONATING, false);
        this.dataTracker.startTracking(TIMER, 0);
        this.dataTracker.startTracking(OWNER, Optional.empty());
    }

    public boolean getDetonating() { return this.dataTracker.get(DETONATING); }
    public PlayerEntity getOwner() {
        Optional<UUID> uuid = this.dataTracker.get(OWNER);
        return uuid.map(value -> this.getWorld().getPlayerByUuid(value)).orElse(null);
    }
    public void setDetonatingTimer(int time) {
        this.dataTracker.set(TIMER, time);
    }

    public void setDetonating(boolean detonating) { this.dataTracker.set(DETONATING, detonating); }

    public void setOwner(PlayerEntity player) {
        this.dataTracker.set(OWNER, Optional.of(player.getUuid()));
    }

    @Override
    public void tick() {
        if (getOwner() != null) {
            if (getDetonating()) {
                if (!getWorld().isClient) this.dataTracker.set(TIMER, this.dataTracker.get(TIMER)-1);
                if (this.dataTracker.get(TIMER) <= 0) {
                    List<Entity> list = getWorld().getOtherEntities(this, Box.of(getPos(), 12, 12,12));
                    for (Entity entity : list) {
                        entity.timeUntilRegen = 9;
                    }
                    this.getWorld().createExplosion(getOwner(), getBlockX(), getBlockY(), getBlockZ(), 2.5f, World.ExplosionSourceType.TNT);
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

    @Override
    public boolean shouldRender(double distance) {
        if (getOwner() == null) return getDetonating();
        return getDetonating() || MinecraftClient.getInstance().player == getOwner();
    }
}
