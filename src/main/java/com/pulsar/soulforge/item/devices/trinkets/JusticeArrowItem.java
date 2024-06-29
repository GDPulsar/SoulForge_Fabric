package com.pulsar.soulforge.item.devices.trinkets;

import com.pulsar.soulforge.entity.JusticeArrowTrinketProjectile;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class JusticeArrowItem extends ArrowItem {
    public JusticeArrowItem() {
        super(new FabricItemSettings().maxCount(64));
    }

    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        return new JusticeArrowTrinketProjectile(world, shooter);
    }
}
