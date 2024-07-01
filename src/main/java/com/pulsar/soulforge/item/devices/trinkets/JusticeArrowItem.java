package com.pulsar.soulforge.item.devices.trinkets;

import com.pulsar.soulforge.entity.JusticeArrowTrinketProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class JusticeArrowItem extends ArrowItem {
    public JusticeArrowItem() {
        super(new Item.Settings().maxCount(64));
    }

    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter, @Nullable ItemStack shotFrom) {
        return new JusticeArrowTrinketProjectile(world, shooter);
    }
}
