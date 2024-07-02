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

public class DomeEmitterEntity extends ThrownItemEntity implements GeoEntity {
    public DomeEmitterEntity(World world, LivingEntity owner) {
        super(SoulForgeEntities.DOME_EMITTER_ENTITY_TYPE, owner, world);
    }

    public DomeEmitterEntity(World world, double d, double e, double f) {
        super(SoulForgeEntities.DOME_EMITTER_ENTITY_TYPE, d, e, f, world);
    }

    public boolean canUsePortals() {
        return false;
    }

    public DomeEmitterEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return SoulForgeItems.DOME_EMITTER;
    }

    @Environment(EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect particleEffect = new DustParticleEffect(Vec3d.unpackRgb(Color.GREEN.getRGB()).toVector3f(), 1f);
            for (int i = 0; i < 8; i++) {
                this.getWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            }
        }
    }

    private final int domeRadius = 3;

    private float lengthSq(int x, int y, int z) {
        return (x*x + y*y + z*z);
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte)3);
            BlockPos center = this.getBlockPos();
            if (hitResult instanceof EntityHitResult entityHitResult) {
                center = entityHitResult.getEntity().getBlockPos();
            }
            this.getWorld().playSound(null, this.getBlockPos(), SoulForgeSounds.DR_RUDEBUSTER_SWING_EVENT, SoundCategory.PLAYERS, 10f, 1f);
            DomeEntity dome = new DomeEntity(this.getWorld(), this.getBlockPos().toCenterPos().subtract(0, 0.5f, 0), domeRadius, 100f, true, (PlayerEntity)this.getOwner());
            dome.setPosition(this.getBlockPos().toCenterPos().subtract(0, 0.5f, 0));
            this.getWorld().spawnEntity(dome);
            double radius = domeRadius + 0.5;
            double radSq = radius * radius;
            double rad1Sq = (radius - 1.5) * (radius - 1.5);
            int ceilRad = MathHelper.ceil(radius);
            for (int x = 0; x <= ceilRad; x++) {
                for (int y = 0; y < ceilRad; y++) {
                    for (int z = 0; z <= ceilRad; z++) {
                        double distanceSq = lengthSq(x, y, z);
                        if (distanceSq > radSq) continue;
                        if (distanceSq < rad1Sq) continue;

                        placeDomeBlock(x, y, z, center, dome);
                        placeDomeBlock(-x, y, z, center, dome);
                        placeDomeBlock(x, -y, z, center, dome);
                        placeDomeBlock(-x, -y, z, center, dome);
                        placeDomeBlock(x, y, -z, center, dome);
                        placeDomeBlock(-x, y, -z, center, dome);
                        placeDomeBlock(x, -y, -z, center, dome);
                        placeDomeBlock(-x, -y, -z, center, dome);
                    }
                }
            }
            this.kill();
        }
    }

    private void placeDomeBlock(int x, int y, int z, BlockPos center, DomeEntity entity) {
        BlockPos pos = new BlockPos(x, y, z).add(center);
        World world = this.getWorld();
        if (!world.isClient) {
            if (pos.toCenterPos().distanceTo(center.toCenterPos()) <= domeRadius) {
                if (!world.getBlockState(pos).isSolid()) {
                    BlockState state = SoulForgeBlocks.DOME_BLOCK.getDefaultState();
                    world.setBlockState(pos, state);
                    DomePart part = new DomePart(entity, x+center.getX(), y+center.getY(), z+center.getZ());
                    this.getWorld().spawnEntity(part);
                    entity.addPart(part);
                }
            }
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (!this.isOwner(player)) {
            super.onPlayerCollision(player);
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
