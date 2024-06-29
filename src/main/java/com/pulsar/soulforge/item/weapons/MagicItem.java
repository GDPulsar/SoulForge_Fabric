package com.pulsar.soulforge.item.weapons;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MagicItem extends Item {
    public MagicItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public boolean isDamageable() { return false; }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }
}
