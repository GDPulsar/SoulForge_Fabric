package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
    public DeterminationShotProjectile(World world, LivingEntity owner) {
        this(SoulForgeEntities.DETERMINATION_SHOT_ENTITY_TYPE, world);
        this.setOwner(owner);
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

    @Override
    protected void initDataTracker() {}

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

    @Override
    protected boolean canHit(Entity entity) {
        if (entity instanceof PlayerEntity targetPlayer && this.getOwner() instanceof PlayerEntity player) {
            if (!TeamUtils.canDamagePlayer(this.getServer(), player, targetPlayer)) return false;
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
        float damage = 5f;
        if (this.getOwner() instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            damage = playerSoul.getEffectiveLV();
        }
        Entity entity = entityHitResult.getEntity();
        Entity entity2 = this.getOwner();
        LivingEntity livingEntity = entity2 instanceof LivingEntity ? (LivingEntity)entity2 : null;
        entity.damage(this.getDamageSources().mobProjectile(this, livingEntity), damage);
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
    public boolean canHit() {
        return true;
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
