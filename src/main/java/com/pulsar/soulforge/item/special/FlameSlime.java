package com.pulsar.soulforge.item.special;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FlameSlime extends Item {
    public FlameSlime() {
        super(new Item.Settings());
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipType context) {
        tooltip.add(Text.literal("What? Why does this have such a high KD ratio?"));
    }
}
