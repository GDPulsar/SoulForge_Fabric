package com.pulsar.soulforge.item.weapons;

import net.minecraft.item.ToolMaterial;

public class MagicToolItem extends MagicItem {
    private final ToolMaterial material = MagicWeaponMaterial.INSTANCE;

    public MagicToolItem() {
        super();
    }

    public ToolMaterial getMaterial() {
        return this.material;
    }

    public int getEnchantability() {
        return this.material.getEnchantability();
    }
}
