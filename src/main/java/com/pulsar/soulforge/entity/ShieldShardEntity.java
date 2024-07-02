package com.pulsar.soulforge.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class ShieldShardEntity extends Entity implements GeoEntity {
    private static final TrackedData<Vector3f> POSITION = DataTracker.registerData(ShieldShardEntity.class, TrackedDataHandlerRegistry.VECTOR3F);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(ShieldShardEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public PlayerEntity owner;
    public float health = 10f;
    public boolean isCircling = false;
    public boolean isLaunched = false;
    private int launchTimer = 0;

    public ShieldShardEntity(PlayerEntity owner) {
        super(SoulForgeEntities.SHIELD_SHARD_ENTITY_TYPE, owner.getWorld());
        this.owner = owner;
        this.dataTracker.set(OWNER_UUID, Optional.of(owner.getUuid()));
        this.setPos(owner.getPos());
        this.setRotation((float)(Math.random()*360f), (float)(Math.random()*360f));
    }

    public boolean canUsePortals() {
        return false;
    }

    public ShieldShardEntity(PlayerEntity owner, Vec3d position, Vec3d velocity) {
        super(SoulForgeEntities.SHIELD_SHARD_ENTITY_TYPE, owner.getWorld());
        this.owner = owner;
        this.dataTracker.set(OWNER_UUID, Optional.of(owner.getUuid()));
        this.setPos(position);
        this.setVelocity(velocity);
        this.setRotation((float)(Math.random()*360f), (float)(Math.random()*360f));
    }

    public ShieldShardEntity(EntityType<ShieldShardEntity> entityType, World world) {
        super(entityType, world);
        this.setRotation((float)(Math.random()*360f), (float)(Math.random()*360f));
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source) || this.owner == null) {
            return false;
        }
        if (source.getAttacker() == this.owner) {
            isLaunched = true;
            launchTimer = 8;
            this.setVelocity(this.owner.getRotationVector());
            return false;
        }
        this.health -= amount;
        if (this.health <= 0f) this.kill();
        return true;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(POSITION, new Vector3f(0, 0, 0));
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    public void setPos(Vec3d pos) {
        this.dataTracker.set(POSITION, pos.toVector3f());
    }

    public Vec3d getPos() {
        Vector3f vec = this.dataTracker.get(POSITION);
        return new Vec3d(vec.x, vec.y, vec.z);
    }

    @Override
    public void tick() {
        Optional<UUID> ownerUUID = this.dataTracker.get(OWNER_UUID);
        ownerUUID.ifPresent(value -> owner = this.getEntityWorld().getPlayerByUuid(value));
        if (this.owner != null) {
            if (isLaunched || !isCircling) {
                for (LivingEntity entity : this.getEntityWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox(), (entity) -> entity != owner)) {
                    entity.damage(this.getDamageSources().mobProjectile(this, owner), 7f);
                }
            }
            if (isLaunched && !isCircling) isLaunched = false;
            if (isLaunched) {
                if (launchTimer > 0) {
                    launchTimer--;
                } else {
                    Vec3d offset = this.owner.getPos().subtract(this.getPos().add(0, -1, 0)).normalize();
                    this.setVelocity(this.getVelocity().add(offset.multiply(0.1f)));
                    if (this.distanceTo(this.owner) <= 2.5f) {
                        this.isLaunched = false;
                    }
                }
            }
            if (this.owner.isDead() || this.owner.isRemoved()) {
                this.kill();
            }
        }
        this.setPitch(this.getPitch()+0.9f);
        this.setYaw(this.getYaw()+6.9f);
        this.setPos(this.getPos().add(this.getVelocity()));
        this.setPosition(this.getPos());
    }

    @Override
    protected Box calculateBoundingBox() {
        return super.calculateBoundingBox().offset(0, -0.5f, 0);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}
    @Override
    public boolean canHit() {
        return true;
    }
    @Override
    public EntityDimensions getDimensions(EntityPose pose) { return EntityDimensions.fixed(0.4f, 1f); }
    @Override
    public boolean shouldSave() { return false; }
    @Override
    public boolean shouldRender(double distance) { return true; }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "main", 0, (event) -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
