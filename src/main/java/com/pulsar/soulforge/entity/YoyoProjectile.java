package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class YoyoProjectile extends ProjectileEntity implements GeoEntity {
    private static final TrackedData<Vector3f> TARGET = DataTracker.registerData(YoyoProjectile.class, TrackedDataHandlerRegistry.VECTOR3F);
    public List<ProjectileEntity> projectiles = new ArrayList<>();

    public YoyoProjectile(World world, LivingEntity owner) {
        this(SoulForgeEntities.YOYO_ENTITY_TYPE, world);
        this.setOwner(owner);
        this.setPosition(owner.getEyePos());
        Vec3d end = owner.getEyePos().add(owner.getRotationVector().multiply(7.5f));
        HitResult hit = owner.getWorld().raycast(new RaycastContext(owner.getEyePos(), end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, owner));
        if (hit != null) end = hit.getPos();
        this.dataTracker.set(TARGET, end.toVector3f());
        this.ignoreCameraFrustum = true;
    }

    public boolean canUsePortals() {
        return false;
    }

    public YoyoProjectile(EntityType<YoyoProjectile> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
    }

    protected void initDataTracker() {
        this.dataTracker.startTracking(TARGET, this.getPos().toVector3f());
    }


    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onCollision(hitResult);
            }
        }
        for (Entity entity : this.getEntityWorld().getOtherEntities(this, Box.of(getPos(), 1, 1, 1), entity -> entity != getOwner() && entity instanceof ProjectileEntity)) {
            if (entity instanceof ProjectileEntity projectile) {
                if (!projectiles.contains(projectile)) {
                    projectiles.add(projectile);
                    entity.remove(RemovalReason.UNLOADED_TO_CHUNK);
                }
            }
        }
        if (this.getOwner() != null) {
            if (this.getOwner().distanceTo(this) >= 25f) this.kill();
        } else {
            this.kill();
        }

        this.checkBlockCollision();
        if (!Objects.equals(this.dataTracker.get(TARGET), new Vector3f()))
            this.setPosition(this.getPos().lerp(Utils.vector3fToVec3d(this.dataTracker.get(TARGET)), 0.8f));
        ProjectileUtil.setRotationFromVelocity(this, 1f);
    }

    protected boolean canHit(Entity entity) {
        return super.canHit(entity) && !entity.noClip;
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean shouldRender(double distance) {
        return true;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.damage(SoulForgeDamageTypes.of(getOwner(), getWorld(), SoulForgeDamageTypes.SUMMON_WEAPON_DAMAGE_TYPE), 4f);
        entity.timeUntilRegen = 15;
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
                if (this.getOwner() instanceof PlayerEntity owner) {
                    living.damage(owner.getDamageSources().playerAttack(owner), 8f);
                    living.timeUntilRegen = 15;
                }
            }
        }
        if (hitResult instanceof BlockHitResult) {

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
