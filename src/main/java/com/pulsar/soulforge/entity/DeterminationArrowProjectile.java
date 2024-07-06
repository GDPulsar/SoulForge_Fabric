package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class DeterminationArrowProjectile extends PersistentProjectileEntity {
    public DeterminationArrowProjectile(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public DeterminationArrowProjectile(World world) {
        super(SoulForgeEntities.DETERMINATION_ARROW_ENTITY_TYPE, world);
    }

    public boolean canUsePortals() {
        return false;
    }

    private int damage;

    public DeterminationArrowProjectile(World world, PlayerEntity owner) {
        super(SoulForgeEntities.DETERMINATION_ARROW_ENTITY_TYPE, world);
        this.setOwner(owner);
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void tick() {
        super.tick();
        this.setVelocity(this.getVelocity().x, this.getVelocity().y - 0.15, this.getVelocity().z);
        if (this.inGround) this.kill();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (getOwner() != null) {
            if (entityHitResult != null) {
                if (entityHitResult.getEntity() instanceof PlayerEntity targetPlayer && this.getOwner() instanceof PlayerEntity player) {
                    if (!TeamUtils.canDamagePlayer(this.getServer(), player, targetPlayer)) return;
                }
                entityHitResult.getEntity().damage(getOwner().getDamageSources().arrow(this, getOwner()), damage);
            }
        }
        this.setVelocity(this.getVelocity().normalize());
        super.onEntityHit(entityHitResult);
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return false;
    }

    @Override
    protected ItemStack asItemStack() {
        return null;
    }
}
