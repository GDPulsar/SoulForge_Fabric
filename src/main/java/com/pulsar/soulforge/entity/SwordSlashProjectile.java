package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SwordSlashProjectile extends ProjectileEntity implements GeoAnimatable {
    private static final TrackedData<Float> DAMAGE = DataTracker.registerData(SwordSlashProjectile.class, TrackedDataHandlerRegistry.FLOAT);

    public SwordSlashProjectile(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public SwordSlashProjectile(World world, PlayerEntity player, float damage) {
        super(SoulForgeEntities.SWORD_SLASH_ENTITY_TYPE, world);
        this.setOwner(player);
        this.setVelocity(player.getRotationVector().withAxis(Direction.Axis.Y, 0).normalize().multiply(1.5f));
        this.dataTracker.set(DAMAGE, damage);
    }

    public boolean canUsePortals() {
        return false;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(DAMAGE, 5f);
    }

    public void tick() {
        super.tick();
        Vec3d vec3d;
        if (!this.getWorld().isClient) {
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onCollision(hitResult);
            }
        }
        this.checkBlockCollision();
        vec3d = this.getVelocity();
        this.setPosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
        this.setVelocity(this.getVelocity().multiply(0.8f));
        if (this.getVelocity().length() <= 0.1) this.kill();
        ProjectileUtil.setRotationFromVelocity(this, 1f);
    }

    protected boolean canHit(Entity entity) {
        if (entity instanceof PlayerEntity targetPlayer && this.getOwner() instanceof PlayerEntity player) {
            if (!TeamUtils.canDamagePlayer(this.getServer(), player, targetPlayer)) return false;
        }
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
        entity.damage(SoulForgeDamageTypes.of(getOwner(), getWorld(), SoulForgeDamageTypes.ABILITY_PROJECTILE_DAMAGE_TYPE), this.dataTracker.get(DAMAGE));
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
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

    @Override
    public double getTick(Object o) {
        return 0;
    }
}
