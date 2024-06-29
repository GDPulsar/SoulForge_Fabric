package com.pulsar.soulforge.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class FragmentationGrenadeProjectile extends ProjectileEntity {
    public FragmentationGrenadeProjectile(World world, Vec3d position, PlayerEntity owner) {
        super(SoulForgeEntities.FRAGMENTATION_GRENADE_ENTITY_TYPE, world);
        this.setOwner(owner);
        this.setPosition(position);
    }

    public FragmentationGrenadeProjectile(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public void tick() {
        if (this.age >= 60) {
            this.kill();
        }
        this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        this.move(MovementType.SELF, this.getVelocity());
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);
        }
        super.tick();
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (!entity.canBeHitByProjectile()) {
            return false;
        }
        return this.getOwner() == null || this.getOwner() != entity;
    }

    @Override
    public void kill() {
        getWorld().createExplosion(this.getOwner(), getX(), getY(), getZ(), 2f, World.ExplosionSourceType.NONE);
        for (int i = 0; i < 70; i++) {
            JusticePelletProjectile pellet = new JusticePelletProjectile(this.getWorld(), (LivingEntity)this.getOwner());
            Vec3d direction = new Vec3d(Math.random()-0.5f, Math.random()-0.5f, Math.random()-0.5f).normalize();
            pellet.setPos(this.getPos().add(direction.multiply(2f)));
            pellet.setVelocity(direction.multiply(4f));
            this.getWorld().spawnEntity(pellet);
        }
        super.kill();
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        this.kill();
    }

    public boolean canHit() {
        return true;
    }

    public boolean damage(DamageSource source, float amount) {
        if (source.getSource() == this.getOwner() || source.getSource() == this) {
            return false;
        }
        if (!this.getWorld().isClient) {
            this.kill();
        }

        return true;
    }
}
