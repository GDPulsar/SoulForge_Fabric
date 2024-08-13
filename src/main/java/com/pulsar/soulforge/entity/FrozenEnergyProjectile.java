package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
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
        if (!this.getWorld().isClient) {
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onCollision(hitResult);
            }
        }

        this.checkBlockCollision();
        Vec3d vel = this.getVelocity();
        this.setVelocity(vel.multiply(1.15f));
        this.setPosition(this.getX() + vel.x, this.getY() + vel.y, this.getZ() + vel.z);
        ProjectileUtil.setRotationFromVelocity(this, 1f);
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (entity instanceof LivingEntity target && this.getOwner() instanceof PlayerEntity player) {
            if (!TeamUtils.canDamageEntity(this.getServer(), player, target)) return false;
        }
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
        if (entity instanceof LivingEntity living && this.age > 20) {
            if (this.getOwner() != null) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul((PlayerEntity) this.getOwner());
                float damage = playerSoul.getEffectiveLV() + 10f;
                DamageSource source = SoulForgeDamageTypes.of(getOwner(), getWorld(), SoulForgeDamageTypes.ABILITY_PROJECTILE_DAMAGE_TYPE);
                living.damage(source, damage);
            }
        }
    }

    private void destroy() {
        this.discard();
        this.getWorld().emitGameEvent(GameEvent.ENTITY_DIE, this.getPos(), GameEvent.Emitter.of(this));
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        this.destroy();
    }
}
