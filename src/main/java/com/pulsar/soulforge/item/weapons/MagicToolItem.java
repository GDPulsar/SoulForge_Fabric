package com.pulsar.soulforge.item.weapons;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;

public class MagicToolItem extends ToolItem {
    public MagicToolItem() {
        super(MagicWeaponMaterial.INSTANCE, new FabricItemSettings().maxCount(1));
    }

    @Override
    public boolean isDamageable() { return false; }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }
}
