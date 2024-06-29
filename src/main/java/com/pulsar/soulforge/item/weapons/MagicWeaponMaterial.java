package com.pulsar.soulforge.item.weapons;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class MagicWeaponMaterial implements ToolMaterial {
    public static final MagicWeaponMaterial INSTANCE = new MagicWeaponMaterial();

    private MagicWeaponMaterial() {}

    @Override
    public int getDurability() {
        return 1;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 0;
    }

    @Override
    public float getAttackDamage() {
        return 0;
    }

    @Override
    public int getMiningLevel() {
        return 0;
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return null;
    }
}
