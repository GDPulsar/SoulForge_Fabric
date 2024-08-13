package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.ValueComponent;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class TotalFrostbiteEntity extends Entity implements Attackable {
    public float maxHealth;
    public float health;
    private LivingEntity target;
    private static final TrackedData<Vector3f> SIZE = DataTracker.registerData(TotalFrostbiteEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    private final PlayerEntity owner;

    public TotalFrostbiteEntity(World world, Vec3d position, float health, LivingEntity target, PlayerEntity owner) {
        super(SoulForgeEntities.TOTAL_FROSTBITE_ENTITY_TYPE, world);
        this.setPosition(position);
        this.maxHealth = health;
        this.health = health;
        this.setEntity(target);
        this.owner = owner;
        this.setSize((float)Math.max(target.getBoundingBox().getXLength(), target.getBoundingBox().getZLength()), (float)target.getBoundingBox().getYLength());
        this.calculateDimensions();
        setBoundingBox(Box.of(this.getPos(), this.getSizeX(), this.getSizeY(), this.getSizeX()));
    }

    public boolean canUsePortals() {
        return false;
    }

    public TotalFrostbiteEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
        maxHealth = 40f;
        health = 40f;
        this.owner = null;
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
        if (this.age > 1) {
            health -= amount;
            if (health <= 0) {
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1f, 1f);
                for (int i = 0; i < 20; i++) {
                    Vec3d pos = new Vec3d(Math.random() * 1.5f - 0.75f, Math.random() * 3f, Math.random() * 1.5f - 0.75f).add(this.getPos());
                    this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.ICE.getDefaultState()), pos.x, pos.y, pos.z, 0f, 0f, 0f);
                }
                ValueComponent values = SoulForge.getValues(target);
                values.removeBool("Immobilized");
                target.setInvulnerable(false);
                kill();
            }
        }
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
