package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.particle.SoulForgeParticles;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class EnergyBallProjectile extends ProjectileEntity {
    public EnergyBallProjectile(World world, LivingEntity owner) {
        this(SoulForgeEntities.ENERGY_BALL_ENTITY_TYPE, world);
        this.setOwner(owner);
    }

    public EnergyBallProjectile(EntityType<EnergyBallProjectile> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }

    @Override
    protected void initDataTracker() {}

    public boolean canUsePortals() {
        return false;
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
        for (int i = 0; i < 4; i++) {
            Vec3d offset = this.getVelocity().add((Math.random() - 0.5f) * 0.05f, (Math.random() - 0.5f) * 0.05f, (Math.random() - 0.5f) * 0.05f);
            Vec3d pos = this.getPos().add(offset);
            Vec3d velocity = offset.normalize().multiply(0.005f);
            this.getWorld().addParticle(SoulForgeParticles.FIRE_PARTICLE, pos.x + (Math.random() - 0.5f)*0.2f,
                    pos.y + (Math.random() - 0.5f)*0.2f, pos.z + (Math.random() - 0.5f)*0.2f, velocity.x, velocity.y, velocity.z);
        }
        this.setPosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
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
        float damage = 1f;
        if (this.getOwner() instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            damage = 3f + playerSoul.getEffectiveLV()*0.75f;
        }
        Entity entity = entityHitResult.getEntity();
        if (entity.damage(SoulForgeDamageTypes.of((PlayerEntity)this.getOwner(), this.getWorld(), SoulForgeDamageTypes.ABILITY_PROJECTILE_DAMAGE_TYPE), damage)) {
            if (this.getOwner() instanceof PlayerEntity player) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                playerSoul.setStyle(playerSoul.getStyle() + (int)damage);
            }
            if (entity instanceof LivingEntity living) {
                living.setFireTicks(250);
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
        if (!getWorld().isClient) {
            for (int i = 0; i < 20; i++) {
                Vec3d pos = new Vec3d(Math.random() - 0.5f, Math.random() - 0.5f, Math.random() - 0.5f);
                Vec3d vel = pos.normalize().multiply(0.5f);
                ((ServerWorld)getWorld()).spawnParticles(ParticleTypes.FLAME, this.getX() + pos.x, this.getY() + pos.y, this.getZ() + pos.z, 1, vel.x, vel.y, vel.z, 0);
            }
        }
        float totalDamage = 0f;
        float aoeDamage = 1f;
        int aoeSize = 3;
        boolean frostburn = false;
        if (this.getOwner() instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            aoeDamage = 2f + playerSoul.getEffectiveLV()/4f;
            aoeSize = Math.max(7, (int)(3 + 2*playerSoul.getEffectiveLV()/6f));
            if (playerSoul.hasTrait(Traits.bravery) && playerSoul.hasTrait(Traits.patience)) {
                frostburn = true;
            }
        }
        for (Entity aoe : Utils.visibleEntitiesInBox(this, Box.of(this.getPos(), aoeSize, aoeSize, aoeSize))) {
            if (aoe instanceof LivingEntity target && target != this.getOwner()) {
                if (target.damage(this.getDamageSources().mobProjectile(this.getOwner(), target), aoeDamage)) {
                    totalDamage += aoeDamage;
                }
                target.setFireTicks(100);
                if (frostburn) target.setFrozenTicks(100);
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
