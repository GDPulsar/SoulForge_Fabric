package com.pulsar.soulforge.entity;

import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class JusticeArrowTrinketProjectile extends PersistentProjectileEntity {
    protected JusticeArrowTrinketProjectile(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public JusticeArrowTrinketProjectile(World world, LivingEntity shooter) {
        super(SoulForgeEntities.JUSTICE_ARROW_TRINKET_TYPE, shooter, world);
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(SoulForgeItems.JUSTICE_ARROW);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age == 15) {
            for (int i = 0; i < 4; i++) {
                ArrowEntity arrowEntity = new ArrowEntity(this.getWorld(), this.getX(), this.getY(), this.getZ());
                Vec3d vel = this.getVelocity().multiply(0.75f);
                arrowEntity.setVelocity(vel.add(new Vec3d(Math.random()-0.2f, Math.random()-0.2f, Math.random()-0.2f)));
                this.getWorld().spawnEntity(arrowEntity);
            }
            this.kill();
        }
    }
}
