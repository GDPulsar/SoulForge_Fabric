package com.pulsar.soulforge.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class JusticeArrowProjectile extends PersistentProjectileEntity {
    public JusticeArrowProjectile(EntityType<JusticeArrowProjectile> entityType, World world) {
        super(entityType, world);
    }

    public boolean canUsePortals() {
        return false;
    }

    public JusticeArrowProjectile(World world) {
        super(SoulForgeEntities.JUSTICE_ARROW_ENTITY_TYPE, world);
        this.pickupType = PickupPermission.DISALLOWED;
        this.setNoGravity(true);
    }

    public JusticeArrowProjectile(World world, PlayerEntity owner) {
        super(SoulForgeEntities.JUSTICE_ARROW_ENTITY_TYPE, world);
        this.setOwner(owner);
        this.pickupType = PickupPermission.DISALLOWED;
        this.setNoGravity(true);
    }

    public void tick() {
        super.tick();
        if (this.age >= 400) {
            this.kill();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        this.setVelocity(this.getVelocity().normalize());
        super.onEntityHit(entityHitResult);
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    protected void onBlockHit(BlockHitResult hitResult) {
        super.onBlockHit(hitResult);
        this.kill();
    }
}
