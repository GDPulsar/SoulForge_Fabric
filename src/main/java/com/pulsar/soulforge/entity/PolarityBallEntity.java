package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PolarityBallEntity extends ProjectileEntity {
    private static final TrackedData<Boolean> INVERSE = DataTracker.registerData(PolarityBallEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public PolarityBallEntity(World world, LivingEntity owner, boolean inverse) {
        this(SoulForgeEntities.POLARITY_BALL_ENTITY_TYPE, world);
        this.setOwner(owner);
        this.setInverse(inverse);
    }

    public boolean canUsePortals() {
        return false;
    }

    public PolarityBallEntity(EntityType<PolarityBallEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(INVERSE, false);
    }

    public void setInverse(boolean inverse) { this.dataTracker.set(INVERSE, inverse); }

    public boolean getInverse() { return this.dataTracker.get(INVERSE); }

    @Override
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
        this.getWorld().addParticle(ParticleTypes.FLAME, this.getX() + Math.random() - 0.5f, this.getY() + Math.random() - 0.5f, this.getZ() + Math.random() - 0.5f, 0.0D, 0.0D, 0.0D);
    }

    @Override
    protected boolean canHit(Entity entity) {
        return super.canHit(entity) && !entity.noClip;
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    private void destroy() {
        this.discard();
        this.getWorld().emitGameEvent(GameEvent.ENTITY_DAMAGE, this.getPos(), GameEvent.Emitter.of(this));
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        float totalDamage = 0f;
        for (Entity entity : this.getEntityWorld().getOtherEntities(null, Box.of(this.getPos(), 24, 24, 24))) {
            if (entity instanceof LivingEntity target && target != this.getOwner()) {
                if (entity instanceof PlayerEntity targetPlayer && this.getOwner() instanceof PlayerEntity player) {
                    if (!TeamUtils.canDamagePlayer(this.getServer(), player, targetPlayer)) return;
                }
                float dist = (float)entity.getPos().distanceTo(this.getPos());
                if (dist <= 12f) {
                    float damage = 10f;
                    if (this.getOwner() instanceof PlayerEntity player) {
                        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                        damage = playerSoul.getEffectiveLV() * 1.25f;
                    }
                    damage *= (12f - dist) / 12f;
                    if (target.damage(SoulForgeDamageTypes.of(getOwner(), getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), damage)) {
                        totalDamage += damage;
                    }
                    Vec3d offset = this.getPos().subtract(entity.getPos()).normalize();
                    if (getInverse()) {
                        entity.setVelocity(offset.multiply(dist/4f));
                    } else {
                        entity.setVelocity(offset.multiply(-(12f-dist)/4f));
                    }
                }
            }
        }
        if (this.getOwner() instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            playerSoul.setStyle(playerSoul.getStyle() + (int)totalDamage);
        }
        super.onCollision(hitResult);
        this.destroy();
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        double d = packet.getVelocityX();
        double e = packet.getVelocityY();
        double f = packet.getVelocityZ();
        this.setVelocity(d, e, f);
    }
}
