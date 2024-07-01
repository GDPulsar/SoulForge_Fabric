package com.pulsar.soulforge.shield;

import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public interface CustomShield {
    int getCoolDownTicks();

    default boolean displayTooltip() {
        return true;
    }

    default void appendShieldTooltip(ItemStack stack, List<Text> tooltip, TooltipType context) {

    }
}
