package com.pulsar.soulforge.entity;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SkullProjectile extends ProjectileEntity implements GeoEntity {
    public SkullProjectile(World world, LivingEntity target) {
        this(SoulForgeEntities.SKULL_ENTITY_TYPE, world);
        this.setOwner(target);
    }

    public SkullProjectile(EntityType<SkullProjectile> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {}

    public boolean canUsePortals() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onCollision(hitResult);
            }
        }
        if (getOwner() != null) {
            if (this.getWorld().getRegistryKey() == getOwner().getWorld().getRegistryKey()) {
                Vec3d offset = getOwner().getEyePos().subtract(this.getPos()).normalize().multiply(0.05f);
                this.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, getOwner().getEyePos());
                this.setPosition(this.getX() + offset.x, this.getY() + offset.y, this.getZ() + offset.z);
                if (this.getPos().distanceTo(this.getOwner().getEyePos()) < 0.5f) {
                    this.getOwner().kill();
                    this.kill();
                }
            }
        }
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

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
