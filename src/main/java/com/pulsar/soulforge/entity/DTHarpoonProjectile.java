package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DTHarpoonProjectile extends ProjectileEntity implements GeoEntity {
    public DTHarpoonProjectile(World world, LivingEntity owner) {
        this(SoulForgeEntities.DT_HARPOON_ENTITY_TYPE, world);
        this.setOwner(owner);
    }

    public DTHarpoonProjectile(EntityType<DTHarpoonProjectile> entityType, World world) {
        super(entityType, world);
    }

    protected void initDataTracker() {

    }

    public boolean canUsePortals() {
        return false;
    }

    private int returningTimer = 0;
    private boolean returning = false;
    private Vec3d returnStart = Vec3d.ZERO;

    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onCollision(hitResult);
            }
        }
        if (this.getOwner() != null) {
            if (this.distanceTo(this.getOwner()) >= 8f) returning = true;
            if (returning) {
                if (returningTimer == 0) returnStart = this.getPos();
                this.setPosition(returnStart.lerp(this.getOwner().getEyePos(), returningTimer / 10f));
                this.returningTimer += 1;
                if (this.returningTimer == 10) {
                    this.destroy();
                }
                return;
            }
        }

        this.checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        this.setPosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            double d = vec3d.horizontalLength();
            this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875));
            this.setPitch((float)(MathHelper.atan2(vec3d.y, d) * 57.2957763671875));
            this.prevYaw = this.getYaw();
            this.prevPitch = this.getPitch();
        }
    }

    protected boolean canHit(Entity entity) {
        return super.canHit(entity) && !entity.noClip;
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean shouldRender(double distance) {
        return distance < 16384.0D;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.damage(SoulForgeDamageTypes.of(getOwner(), getWorld(), SoulForgeDamageTypes.SUMMON_WEAPON_DAMAGE_TYPE), 12f);
    }

    private void destroy() {
        this.discard();
        this.getWorld().emitGameEvent(GameEvent.ENTITY_DAMAGE, this.getPos(), GameEvent.Emitter.of(this));
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (this.getOwner() == null) this.destroy();
        if (hitResult instanceof EntityHitResult entityHitResult) {
            if (entityHitResult.getEntity() instanceof LivingEntity living) {
                living.setVelocity(this.getOwner().getPos().subtract(living.getPos()).normalize().multiply(2f));
                living.velocityModified = true;
            }
        }
        if (hitResult instanceof BlockHitResult) {
            this.returning = true;
            this.returningTimer = 0;
            this.returnStart = this.getPos();
        }
        this.destroy();
    }

    public boolean canHit() {
        return true;
    }

    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        double d = packet.getVelocityX();
        double e = packet.getVelocityY();
        double f = packet.getVelocityZ();
        this.setVelocity(d, e, f);
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
