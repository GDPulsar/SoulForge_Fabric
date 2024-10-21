package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DeterminationStaffStarProjectile extends ProjectileEntity {
    public DeterminationStaffStarProjectile(World world, LivingEntity thrower) {
        super(SoulForgeEntities.STAFF_STAR_ENTITY_TYPE, world);
        this.setOwner(thrower);
    }

    public DeterminationStaffStarProjectile(EntityType<DeterminationStaffStarProjectile> entityType, World world) {
        super(entityType, world);
    }

    public boolean canUsePortals() {
        return false;
    }

    @Override
    protected void initDataTracker() {}

    @Override
    protected boolean canHit(Entity entity) {
        if (entity instanceof LivingEntity target && this.getOwner() instanceof PlayerEntity player) {
            if (!TeamUtils.canDamageEntity(this.getServer(), player, target)) return false;
        }
        return super.canHit(entity) && !entity.noClip;
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

        this.updateRotation();
        this.checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        this.setPosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (getWorld().isClient) {
            for (int i = 0; i < 10; i++) {
                getWorld().addParticle(ParticleTypes.FIREWORK, this.getX(), this.getY(), this.getZ(), Math.random() - 0.5f, Math.random() - 0.5f, Math.random() - 0.5f);
            }
        } else {
            getWorld().playSound(null, getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.MASTER, 1f, 2f);
            this.kill();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (getWorld().isClient) {
            for (int i = 0; i < 10; i++) {
                getWorld().addParticle(ParticleTypes.FIREWORK, this.getX(), this.getY(), this.getZ(), Math.random() - 0.5f, Math.random() - 0.5f, Math.random() - 0.5f);
            }
        } else {
            if (getOwner() instanceof PlayerEntity player && entity instanceof LivingEntity living) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                if (living.damage(SoulForgeDamageTypes.of(player, getWorld(), SoulForgeDamageTypes.DETERMINATION_STAR_DAMAGE_TYPE), playerSoul.getEffectiveLV() * 0.75f)) {
                    getWorld().playSound(null, getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.MASTER, 1f, 2f);
                    this.kill();
                }
            }
        }
    }
}
