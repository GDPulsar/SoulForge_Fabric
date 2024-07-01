package com.pulsar.soulforge.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class JusticeArrowProjectile extends ProjectileEntity {
    private static final TrackedData<Vector3f> POSITION = DataTracker.registerData(JusticeArrowProjectile.class, TrackedDataHandlerRegistry.VECTOR3F);
    private static final TrackedData<Vector3f> VELOCITY = DataTracker.registerData(JusticeArrowProjectile.class, TrackedDataHandlerRegistry.VECTOR3F);

    public JusticeArrowProjectile(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public JusticeArrowProjectile(World world) {
        super(SoulForgeEntities.JUSTICE_ARROW_ENTITY_TYPE, world);
    }

    private PlayerEntity owner;
    private float damage;

    public JusticeArrowProjectile(World world, PlayerEntity owner) {
        super(SoulForgeEntities.JUSTICE_ARROW_ENTITY_TYPE, world);
        this.owner = owner;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setVelocity(Vector3f velocity) {
        this.dataTracker.set(VELOCITY, velocity);
    }

    public void setPosition(Vector3f position) {
        this.dataTracker.set(POSITION, position);
    }

    public Vector3f getVel() {
        return this.dataTracker.get(VELOCITY);
    }


    public Vector3f getPosition() {
        return this.dataTracker.get(POSITION);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(POSITION, new Vector3f());
        builder.add(VELOCITY, new Vector3f());
    }

    public void tick() {
        super.tick();
        Vec3d vec3d;
        if (!this.getWorld().isClient) {
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult != null) {
                if (hitResult.getType() != HitResult.Type.MISS) {
                    this.onCollision(hitResult);
                }
            }
        }
        if (this.age >= 400) {
            this.kill();
        }

        this.checkBlockCollision();
        vec3d = this.getVelocity();
        this.setPosition(new Vector3f((float)(this.getX() + vec3d.x), (float)(this.getY() + vec3d.y), (float)(this.getZ() + vec3d.z)));
        this.setPosition(this.getPosition().x, this.getPosition().y, this.getPosition().z);
        ProjectileUtil.setRotationFromVelocity(this, 0.5F);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (owner != null) {
            if (entityHitResult != null) {
                DamageSource source = this.getDamageSources().mobProjectile(this, null);
                if (getOwner() != null) source = getOwner().getDamageSources().mobProjectile(this, (LivingEntity)getOwner());
                entityHitResult.getEntity().damage(source, damage);
            }
        }
        super.onEntityHit(entityHitResult);
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        this.kill();
    }
}
