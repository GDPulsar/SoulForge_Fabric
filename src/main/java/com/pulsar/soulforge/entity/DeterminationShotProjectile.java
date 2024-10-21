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
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class DeterminationShotProjectile extends ProjectileEntity {
    private static final TrackedData<Float> DAMAGE = DataTracker.registerData(JusticePelletProjectile.class, TrackedDataHandlerRegistry.FLOAT);

    public DeterminationShotProjectile(World world, LivingEntity owner) {
        this(SoulForgeEntities.DETERMINATION_SHOT_ENTITY_TYPE, world);
        this.setOwner(owner);
        if (owner instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            this.dataTracker.set(DAMAGE, 2f + playerSoul.getEffectiveLV() / 4f);
        }
    }

    public DeterminationShotProjectile(World world, LivingEntity owner, float damage) {
        this(SoulForgeEntities.DETERMINATION_SHOT_ENTITY_TYPE, world);
        this.setOwner(owner);
        this.dataTracker.set(DAMAGE, damage);
    }

    public DeterminationShotProjectile(EntityType<DeterminationShotProjectile> entityType, World world) {
        super(entityType, world);
    }

    public boolean canUsePortals() {
        return false;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }

    public void setPos(Vec3d pos) {
        this.setPosition(pos);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(DAMAGE, 2f);
    }

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
    }

    public float getDamage() {
        return this.dataTracker.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.dataTracker.set(DAMAGE, damage);
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (entity instanceof LivingEntity target && this.getOwner() instanceof PlayerEntity player) {
            if (!TeamUtils.canDamageEntity(this.getServer(), player, target)) return false;
        }
        return super.canHit(entity) && !entity.noClip;
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if (entity.damage(SoulForgeDamageTypes.of(getOwner(), getWorld(), SoulForgeDamageTypes.ABILITY_PROJECTILE_DAMAGE_TYPE), getDamage())) {
            if (getOwner() instanceof PlayerEntity player) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                playerSoul.setStyle(playerSoul.getStyle() + (int)getDamage());
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
    }

    private void destroy() {
        this.discard();
        this.getWorld().emitGameEvent(GameEvent.ENTITY_DAMAGE, this.getPos(), GameEvent.Emitter.of(this));
    }

    @Override
    protected void onCollision(HitResult hitResult) {
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
