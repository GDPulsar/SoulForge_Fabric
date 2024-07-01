package com.pulsar.soulforge.item.special;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NebulousBread extends Item {
    public NebulousBread() {
        super(new Item.Settings().food(new FoodComponent.Builder().nutrition(100).saturationModifier(100f).build()));
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipType context) {
        tooltip.add(Text.literal("It's bread? But... That makes no sense!"));
    }
}
