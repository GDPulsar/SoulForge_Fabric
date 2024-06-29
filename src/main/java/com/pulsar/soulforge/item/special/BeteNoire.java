package com.pulsar.soulforge.item.special;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BeteNoire extends Item {
    public BeteNoire() {
        super(new FabricItemSettings().food(new FoodComponent.Builder().hunger(6).saturationModifier(1f).build()));
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("This... Doesn't feel right..."));
    }
}
