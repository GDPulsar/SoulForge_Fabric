package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class FrozenEnergyProjectile extends ProjectileEntity {
    public FrozenEnergyProjectile(World world, LivingEntity owner) {
        this(SoulForgeEntities.FROZEN_ENERGY_ENTITY_TYPE, world);
        this.setOwner(owner);
    }

    public FrozenEnergyProjectile(EntityType<FrozenEnergyProjectile> entityType, World world) {
        super(entityType, world);
    }

    public boolean canUsePortals() {
        return false;
    }

    protected void initDataTracker() {

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
        ProjectileUtil.setRotationFromVelocity(this, 1f);
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
        Entity entity2 = this.getOwner();
        LivingEntity livingEntity = entity2 instanceof LivingEntity ? (LivingEntity)entity2 : null;
        float damage = 6f;
        if (this.getOwner() != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul((PlayerEntity)this.getOwner());
            damage = playerSoul.getEffectiveLV()*0.4f;
        }
        if (entity instanceof LivingEntity living) {
            for (StatusEffectInstance effect : living.getStatusEffects()) {
                if (!effect.getEffectType().isBeneficial() && effect.getEffectType() != StatusEffects.UNLUCK) damage += effect.getAmplifier();
            }
        }
        entity.damage(this.getDamageSources().mobProjectile(this, livingEntity), damage);
    }

    private void destroy() {
        this.discard();
        this.getWorld().emitGameEvent(GameEvent.ENTITY_DAMAGE, this.getPos(), GameEvent.Emitter.of(this));
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        this.destroy();
    }

    public boolean canHit() {
        return true;
    }

    public boolean damage(DamageSource source, float amount) {
        if (!this.getWorld().isClient) {
            ((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
            this.destroy();
        }

        return true;
    }

    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        double d = packet.getVelocityX();
        double e = packet.getVelocityY();
        double f = packet.getVelocityZ();
        this.setVelocity(d, e, f);
    }
}
