package com.pulsar.soulforge.item.special;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BeteNoire extends Item {
    public BeteNoire() {
        super(new Item.Settings().food(new FoodComponent.Builder().nutrition(6).saturationModifier(1f).build()));
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipType context) {
        tooltip.add(Text.literal("This... Doesn't feel right..."));
    }
}
