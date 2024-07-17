package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.block.SoulForgeBlocks;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DomeEntity extends Entity implements Attackable {
    private static final TrackedData<Float> MAX_HEALTH = DataTracker.registerData(DomeEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> HEALTH = DataTracker.registerData(DomeEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> SIZE = DataTracker.registerData(DomeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> EMITTER = DataTracker.registerData(DomeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SHIELD_BREAK_IMMUNE = DataTracker.registerData(DomeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private final PlayerEntity owner;

    public boolean canUsePortals() {
        return false;
    }

    public DomePart[] parts = new DomePart[]{};

    public DomeEntity(World world, Vec3d position, int size, float health) {
        super(SoulForgeEntities.DOME_ENTITY_TYPE, world);
        this.setPosition(position);
        setMaxHealth(health);
        setHealth(health);
        setSize(size);
        setEmitter(false);
        setShieldBreakImmune(false);
        this.owner = null;
    }

    public DomeEntity(World world, Vec3d position, int size, float health, boolean domeEmitter, PlayerEntity owner) {
        super(SoulForgeEntities.DOME_ENTITY_TYPE, world);
        this.setPosition(position);
        setMaxHealth(health);
        setHealth(health);
        setSize(size);
        setEmitter(domeEmitter);
        setShieldBreakImmune(false);
        this.owner = owner;
    }

    public DomeEntity(World world, Vec3d position, int size, float health, boolean domeEmitter, PlayerEntity owner, boolean shieldBreakImmune) {
        super(SoulForgeEntities.DOME_ENTITY_TYPE, world);
        this.setPosition(position);
        setMaxHealth(health);
        setHealth(health);
        setSize(size);
        setEmitter(domeEmitter);
        setShieldBreakImmune(shieldBreakImmune);
        this.owner = owner;
    }

    public DomeEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
        setMaxHealth(100f);
        setHealth(100f);
        setSize(4);
        setEmitter(false);
        setShieldBreakImmune(false);
        this.owner = null;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(MAX_HEALTH, 100.0f);
        this.dataTracker.startTracking(HEALTH, 100.0f);
        this.dataTracker.startTracking(SIZE, 4);
        this.dataTracker.startTracking(EMITTER, false);
        this.dataTracker.startTracking(SHIELD_BREAK_IMMUNE, false);
    }

    public void addPart(DomePart part) {
        List<DomePart> newParts = new ArrayList<>();
        Collections.addAll(newParts, this.parts);
        newParts.add(part);
        this.parts = newParts.toArray(new DomePart[0]);
    }

    private void setMaxHealth(float health) {
        this.dataTracker.set(MAX_HEALTH, health);
    }

    private void setHealth(float health) {
        this.dataTracker.set(HEALTH, health);
    }

    private void setSize(int size) {
        this.dataTracker.set(SIZE, size);
    }

    private void setEmitter(boolean emitter) {
        this.dataTracker.set(EMITTER, emitter);
    }

    private void setShieldBreakImmune(boolean shieldBreakImmune) {
        this.dataTracker.set(SHIELD_BREAK_IMMUNE, shieldBreakImmune);
    }

    public float getMaxHealth() {
        return this.dataTracker.get(MAX_HEALTH);
    }

    public float getHealth() {
        return this.dataTracker.get(HEALTH);
    }

    public int getSize() {
        return this.dataTracker.get(SIZE);
    }

    public boolean getEmitter() {
        return this.dataTracker.get(EMITTER);
    }

    public boolean getShieldBreakImmune() {
        return this.dataTracker.get(SHIELD_BREAK_IMMUNE);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        setSize(nbt.getInt("size"));
        setMaxHealth(nbt.getFloat("maxHealth"));
        setHealth(nbt.getFloat("health"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("size", getSize());
        nbt.putFloat("maxHealth", getMaxHealth());
        nbt.putFloat("health", getHealth());
    }

    @Nullable
    @Override
    public LivingEntity getLastAttacker() {
        return null;
    }

    @Override
    public void tick() {
        for (DomePart part : this.parts) {
            part.tick();
        }
        if (this.owner != null) {
            if (this.owner.isDead() || this.owner.isRemoved()) {
                this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 10f, 1f);
                this.kill();
            }
        }
        super.tick();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.getShieldBreakImmune()) {
            if (source.getAttacker() instanceof PlayerEntity attacker) {
                SoulComponent attackerSoul = SoulForge.getPlayerSoul(attacker);
                if (attackerSoul.hasValue("shieldBreak")) {
                    amount *= attackerSoul.getValue("shieldBreak");
                }
            }
        }
        setHealth(getHealth() - amount);
        if (this.owner != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(this.owner);
            playerSoul.setStyle(playerSoul.getStyle() + (int)amount);
        }
        if (source.getAttacker() == this.owner && this.getEmitter()) {
            setHealth(-1f);
        }
        if (getHealth() <= 0) {
            if (this.getEmitter()) {
                int domeRadius = this.getSize();
                for (int x = -domeRadius; x <= domeRadius; x++) {
                    for (int y = -domeRadius; y <= domeRadius; y++) {
                        for (int z = -domeRadius; z <= domeRadius; z++) {
                            BlockPos pos = new BlockPos(x, y, z).add(this.getBlockPos());
                            if (this.getWorld().getBlockState(pos).isOf(SoulForgeBlocks.DOME_BLOCK)) {
                                this.getWorld().addBlockBreakParticles(pos, this.getWorld().getBlockState(pos));
                                this.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
                            }
                        }
                    }
                }
                for (DomePart part : this.getParts()) {
                    if (!part.isRemoved()) part.remove(RemovalReason.KILLED);
                }
                this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 10f, 1f);
            }
            kill();
        }
        return true;
    }

    public DomePart[] getParts() {
        return this.parts;
    }

    @Override
    public boolean canHit() {
        return false;
    }

    @Override
    public boolean shouldRender(double distance) {
        return false;
    }
}
