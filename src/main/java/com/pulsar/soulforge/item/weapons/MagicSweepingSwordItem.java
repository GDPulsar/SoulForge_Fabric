package com.pulsar.soulforge.item.weapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public class MagicSweepingSwordItem extends MagicSwordItem {
    public MagicSweepingSwordItem(float attackDamage, float attackSpeed, float lvIncrease) {
        super(attackDamage, attackSpeed, lvIncrease);
    }
}
