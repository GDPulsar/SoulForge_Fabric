package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.block.SoulForgeBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

    public DomePart(DomeEntity owner, int x, int y, int z) {
        super(SoulForgeEntities.DOME_PART_TYPE, owner.getWorld());
        this.owner = owner;
        this.dataTracker.set(OWNER_UUID, Optional.of(this.owner.getUuid()));
        this.setPosition(x+0.5f, y, z+0.5f);
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
        BlockState state = this.getWorld().getBlockState(this.getBlockPos());
        if (!state.isOf(SoulForgeBlocks.DOME_BLOCK) && !this.dataTracker.get(DETERMINATION)) {
            this.getWorld().setBlockState(this.getBlockPos(), SoulForgeBlocks.DOME_BLOCK.getDefaultState());
        }
        if (!state.isOf(SoulForgeBlocks.DETERMINATION_DOME_BLOCK) && this.dataTracker.get(DETERMINATION)) {
            this.getWorld().setBlockState(this.getBlockPos(), SoulForgeBlocks.DETERMINATION_DOME_BLOCK.getDefaultState());
        }
        if (this.owner != null) {
            if (this.owner.isRemoved()) {
                this.getWorld().setBlockState(this.getBlockPos(), Blocks.AIR.getDefaultState());
                this.kill();
            }
        }
        super.tick();
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(OWNER_UUID, Optional.empty());
        builder.add(DETERMINATION, false);
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
        return super.calculateBoundingBox().offset(0, -0.05f, 0);
    }
    @Override
    public boolean shouldSave() { return false; }
    @Override
    public boolean shouldRender(double distance) { return distance < 64; }
}
