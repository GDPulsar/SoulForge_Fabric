package com.pulsar.soulforge.item.special;

import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CardboardTube extends Item {
    public CardboardTube() {
        super(new FabricItemSettings());
    }

    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.takeKnockback(2f, attacker.getX()-target.getX(), attacker.getZ()-target.getZ());
        target.getWorld().playSound(null, target.getBlockPos(), SoulForgeSounds.BONK_EVENT, SoundCategory.MASTER, 1f, 1f);
        return false;
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("anti-cat measure"));
    }
}
