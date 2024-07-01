package com.pulsar.soulforge.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class GrappleHookProjectile extends ProjectileEntity implements GeoEntity {
    public GrappleHookProjectile(World world, LivingEntity thrower) {
        super(SoulForgeEntities.GRAPPLE_HOOK_ENTITY_TYPE, world);
        this.setOwner(thrower);
        thrower.setPosition(thrower.getEyePos());
        thrower.setVelocity(thrower.getRotationVector().multiply(1.5f));
    }

    public GrappleHookProjectile(EntityType<GrappleHookProjectile> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    public List<Vec3d> positions = new ArrayList<>();

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onCollision(hitResult);
            }
            if (this.getOwner() != null) {
                Vec3d to = this.getOwner().getPos();
                if (!positions.isEmpty()) to = positions.get(positions.size()-1);
                RaycastContext context = new RaycastContext(this.getPos(), to, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this);
                BlockHitResult hit = this.getWorld().raycast(context);
                if (hit != null) {
                    if (hit.getPos().distanceTo(to) > 0.5f) {
                        positions.add(hit.getPos());
                    }
                }
            }
        }

        this.checkBlockCollision();
        this.setVelocity(this.getVelocity().subtract(0, 0.04, 0));
        Vec3d vec3d = this.getVelocity();
        this.setPosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
    }

    @Override
    public void onBlockHit(BlockHitResult hit) {
        if (this.getOwner() instanceof PlayerEntity player) {
            if (positions.isEmpty()) positions.add(this.getPos());
            Vec3d offset = positions.get(0).subtract(player.getPos());
            player.setVelocity(offset.normalize().multiply(Math.sqrt(offset.length())));
            player.velocityModified = true;
        }
        this.kill();
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
