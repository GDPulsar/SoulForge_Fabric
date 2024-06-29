package com.pulsar.soulforge.item.special;

import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RespondingFreenix extends Item {
    public RespondingFreenix() {
        super(new FabricItemSettings().maxCount(1));
    }

    public int nameIndex = 0;
    public int indexTimer = 0;

    @Override
    public Text getName() {
        return switch (nameIndex) {
            case 0 -> Text.literal("Responding Freenix");
            case 1 -> Text.literal("Respirant Larynx");
            case 2 -> Text.literal("Reglongant Panopticon");
            case 3 -> Text.literal("Mayodenociae");
            case 4 -> Text.literal("Regurgatant Minos Prime");
            case 5 -> Text.literal("repugnant pissant");
            case 6 -> Text.literal("Repulsive Precipitation");
            case 7 -> Text.literal("jorking peanuts");
            case 8 -> Text.literal("Retired Lint");
            case 9 -> Text.literal("redoubled pilkmonger");
            default -> Text.literal("am i gregnant?");
        };
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient && !selected) {
            indexTimer++;
            if (indexTimer >= 100) {
                nameIndex = (int)(Math.random()*10);
                indexTimer = 0;
            }
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        return getName();
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("[[EXTREMELY LOUD INCORRECT BUZZER]]"));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.playSound(SoulForgeSounds.BUZZER_EVENT, 1f, 1f);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
