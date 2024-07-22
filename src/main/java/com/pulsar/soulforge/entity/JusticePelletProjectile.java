package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.function.Consumer;

public class JusticePelletProjectile extends ProjectileEntity {
    private static final TrackedData<Float> DAMAGE = DataTracker.registerData(JusticePelletProjectile.class, TrackedDataHandlerRegistry.FLOAT);
    private Consumer<LivingEntity> onDamageEvent = null;

    public JusticePelletProjectile(World world, LivingEntity owner) {
        this(SoulForgeEntities.JUSTICE_PELLET_ENTITY_TYPE, world);
        this.setOwner(owner);
        if (owner instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            this.dataTracker.set(DAMAGE, 2f + playerSoul.getEffectiveLV() / 4f);
        }
    }

    public boolean canUsePortals() {
        return false;
    }

    public JusticePelletProjectile(World world, LivingEntity owner, float damage) {
        this(SoulForgeEntities.JUSTICE_PELLET_ENTITY_TYPE, world);
        this.setOwner(owner);
        this.dataTracker.set(DAMAGE, damage);
    }

    public JusticePelletProjectile(World world, LivingEntity owner, float damage, Consumer<LivingEntity> onDamageEvent) {
        this(SoulForgeEntities.JUSTICE_PELLET_ENTITY_TYPE, world);
        this.setOwner(owner);
        this.dataTracker.set(DAMAGE, damage);
        this.onDamageEvent = onDamageEvent;
    }

    public JusticePelletProjectile(EntityType<JusticePelletProjectile> entityType, World world) {
        super(entityType, world);
    }

    public void setPos(Vec3d pos) {
        this.setPosition(pos);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(DAMAGE, 2f);
    }

    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult != null) {
                if (hitResult.getType() != HitResult.Type.MISS) {
                    this.onCollision(hitResult);
                }
            }
        }

        this.checkBlockCollision();
        Vec3d vel = this.getVelocity();
        this.setPos(new Vec3d(this.getX() + vel.x, this.getY() + vel.y, this.getZ() + vel.z));
        ProjectileUtil.setRotationFromVelocity(this, 0.5F);
    }

    public float getDamage() {
        return this.dataTracker.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.dataTracker.set(DAMAGE, damage);
    }

    protected boolean canHit(Entity entity) {
        if (entity instanceof PlayerEntity targetPlayer && this.getOwner() instanceof PlayerEntity player) {
            if (this.dataTracker.get(DAMAGE) > 0 && !TeamUtils.canDamagePlayer(this.getServer(), player, targetPlayer)) return false;
            if (this.dataTracker.get(DAMAGE) < 0 && !TeamUtils.canHealPlayer(this.getServer(), player, targetPlayer)) return false;
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
        if (entity instanceof LivingEntity living) {
            DamageSource source;
            if (getOwner() instanceof AutoTurretEntity autoTurret) {
                source = SoulForgeDamageTypes.of(autoTurret.getOwner(), this.getWorld(), SoulForgeDamageTypes.AUTO_TURRET_DAMAGE_TYPE);
            } else {
                source = SoulForgeDamageTypes.of(getOwner(), getWorld(), SoulForgeDamageTypes.ABILITY_PROJECTILE_DAMAGE_TYPE);
            }
            float damage = this.dataTracker.get(DAMAGE);
            if (damage > 0) {
                if (living.damage(source, damage)) {
                    if (this.getOwner() instanceof PlayerEntity player) {
                        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                        playerSoul.setStyle(playerSoul.getStyle() + (int)damage);
                    }
                    entity.timeUntilRegen = 0;
                    if (this.onDamageEvent != null) {
                        this.onDamageEvent.accept(living);
                    }
                }
            }
            else {
                if (living.getHealth() < living.getMaxHealth()) {
                    if (this.getOwner() instanceof PlayerEntity player) {
                        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                        playerSoul.setStyle(playerSoul.getStyle() - (int) damage);
                    }
                }
                living.heal(-damage);
                living.getWorld().playSound(null, living.getBlockPos(), SoulForgeSounds.UT_HEAL_EVENT, SoundCategory.MASTER, 1f, 1f);
            }
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

    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        double d = packet.getVelocityX();
        double e = packet.getVelocityY();
        double f = packet.getVelocityZ();
        this.setVelocity(d, e, f);
    }
}
