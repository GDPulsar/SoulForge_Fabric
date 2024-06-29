package com.pulsar.soulforge.item.special;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NebulousBread extends Item {
    public NebulousBread() {
        super(new FabricItemSettings().food(new FoodComponent.Builder().hunger(100).saturationModifier(100f).build()));
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("It's bread? But... That makes no sense!"));
    }
}
