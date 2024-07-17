package com.pulsar.soulforge.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FragmentationGrenadeProjectile extends ProjectileEntity {
    public FragmentationGrenadeProjectile(World world, Vec3d position, PlayerEntity owner) {
        super(SoulForgeEntities.FRAGMENTATION_GRENADE_ENTITY_TYPE, world);
        this.setOwner(owner);
        this.setPosition(position);
    }

    public FragmentationGrenadeProjectile(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public boolean canUsePortals() {
        return false;
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
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        this.move(MovementType.SELF, this.getVelocity());
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.onCollision(hitResult);
        }
        super.tick();
    }

    @Override
    public boolean canBeHitByProjectile() {
        return true;
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
        DamageSource source = this.getDamageSources().explosion(this, getOwner());
        getWorld().createExplosion(this.getOwner(), source, null, getX(), getY(), getZ(), 2f, false, World.ExplosionSourceType.NONE);
        for (int i = 0; i < 70; i++) {
            JusticePelletProjectile pellet = new JusticePelletProjectile(this.getWorld(), (LivingEntity)this.getOwner());
            Vec3d direction = new Vec3d(Math.random()-0.5f, Math.random()*1.5f-0.75f, Math.random()-0.5f).normalize();
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
        if (source.getSource() == this) {
            return false;
        }
        this.kill();
        return true;
    }
}
