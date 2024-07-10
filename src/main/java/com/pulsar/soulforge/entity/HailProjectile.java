package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class HailProjectile extends ProjectileEntity {
    private static final TrackedData<Boolean> IS_STAGE_1 = DataTracker.registerData(HailProjectile.class, TrackedDataHandlerRegistry.BOOLEAN);

    public HailProjectile(World world, LivingEntity owner, boolean stage1) {
        this(SoulForgeEntities.HAIL_ENTITY_TYPE, world);
        this.setOwner(owner);
        this.dataTracker.set(IS_STAGE_1, stage1);
    }

    public boolean canUsePortals() {
        return false;
    }

    public HailProjectile(EntityType<HailProjectile> entityType, World world) {
        super(entityType, world);
    }

    protected void initDataTracker() {
        this.dataTracker.startTracking(IS_STAGE_1, false);
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
        if (entity.damage(SoulForgeDamageTypes.of(getOwner(), getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), 12f)) {
            if (this.getOwner() instanceof PlayerEntity player && entity instanceof LivingEntity living) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                playerSoul.setStyle(playerSoul.getStyle() + (int)(12f * (1f + Utils.getTotalDebuffLevel(living)/10f)));
            }
        }
        if (this.dataTracker.get(IS_STAGE_1) && entity instanceof LivingEntity living) {
            living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, 300, 1));
        }
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

    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        double d = packet.getVelocityX();
        double e = packet.getVelocityY();
        double f = packet.getVelocityZ();
        this.setVelocity(d, e, f);
    }
}
