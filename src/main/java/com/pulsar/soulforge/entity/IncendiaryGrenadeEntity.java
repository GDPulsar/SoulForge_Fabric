package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.block.SoulForgeBlocks;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.awt.*;

public class IncendiaryGrenadeEntity extends ThrownItemEntity implements GeoEntity {
    public IncendiaryGrenadeEntity(World world, LivingEntity owner) {
        super(SoulForgeEntities.INCENDIARY_GRENADE_ENTITY_TYPE, owner, world);
    }

    public IncendiaryGrenadeEntity(World world, double d, double e, double f) {
        super(SoulForgeEntities.INCENDIARY_GRENADE_ENTITY_TYPE, d, e, f, world);
    }

    public IncendiaryGrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return SoulForgeItems.INCENDIARY_GRENADE;
    }

    @Override
    public void tick() {
        super.tick();
        this.setYaw((this.getYaw()+10f)%360);
        this.setPitch((this.getPitch()+8.26f)%360);
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 2f, true, World.ExplosionSourceType.TNT);
            this.kill();
        }
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
