package com.pulsar.soulforge.item.devices;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.TraitedArniciteCoreItem;
import com.pulsar.soulforge.item.TraitedArniciteHeartItem;
import com.pulsar.soulforge.item.TraitedArniciteItem;
import com.pulsar.soulforge.trait.TraitBase;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

public class ArmorDeviceBase extends ArmorItem implements Equipment {
    public final int maxCharge;
    public final TraitBase trait;

    public ArmorDeviceBase(ArmorMaterial material, ArmorItem.Type type, Item.Settings settings, int maxCharge, TraitBase trait) {
        super(material, type, settings);
        this.maxCharge = maxCharge;
        this.trait = trait;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (getCharge(stack) < maxCharge) {
                ItemStack offhand = user.getOffHandStack();
                if (offhand.getItem() instanceof TraitedArniciteItem item) {
                    if (item.trait != this.trait) return TypedActionResult.pass(user.getStackInHand(hand));
                    offhand.decrement(1);
                    user.giveItemStack(new ItemStack(SoulForgeItems.ARNICITE));
                    increaseCharge(stack, 50);
                    return TypedActionResult.success(user.getStackInHand(hand));
                }
                if (offhand.getItem() instanceof TraitedArniciteHeartItem item) {
                    if (item.trait != this.trait) return TypedActionResult.pass(user.getStackInHand(hand));
                    offhand.decrement(1);
                    user.giveItemStack(new ItemStack(SoulForgeItems.ARNICITE_HEART));
                    increaseCharge(stack, 100);
                    return TypedActionResult.success(user.getStackInHand(hand));
                }
                if (offhand.getItem() instanceof TraitedArniciteCoreItem item) {
                    if (item.trait != this.trait) return TypedActionResult.pass(user.getStackInHand(hand));
                    offhand.decrement(1);
                    user.giveItemStack(new ItemStack(SoulForgeItems.ARNICITE_CORE));
                    increaseCharge(stack, 1000);
                    return TypedActionResult.success(user.getStackInHand(hand));
                }
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    public int getCharge(ItemStack stack) {
        return ((stack.getItem() instanceof DeviceBase || stack.getItem() instanceof PickaxeDeviceBase || stack.getItem() instanceof ArmorDeviceBase) && stack.hasNbt() && stack.getNbt().contains("charge")) ? stack.getNbt().getInt("charge") : 0;
    }

    public void setCharge(ItemStack stack, int charge) {
        if (stack.getItem() instanceof DeviceBase || stack.getItem() instanceof PickaxeDeviceBase || stack.getItem() instanceof ArmorDeviceBase) {
            if (!stack.hasNbt()) stack.getOrCreateNbt();
            stack.getNbt().putInt("charge", Math.min(Math.max(charge, 0), maxCharge));
        }
    }

    public void increaseCharge(ItemStack stack, int amount) {
        setCharge(stack, getCharge(stack) + amount);
    }

    public void decreaseCharge(ItemStack stack, int amount) {
        setCharge(stack, getCharge(stack) - amount);
    }

    public void tryCharge(PlayerEntity player) {
        ItemStack stack;
        if (player.getMainHandStack().getItem() instanceof DeviceBase || player.getMainHandStack().getItem() instanceof PickaxeDeviceBase || player.getMainHandStack().getItem() instanceof ArmorDeviceBase) stack = player.getMainHandStack();
        else if (player.getOffHandStack().getItem() instanceof DeviceBase || player.getMainHandStack().getItem() instanceof PickaxeDeviceBase || player.getMainHandStack().getItem() instanceof ArmorDeviceBase) stack = player.getOffHandStack();
        else return;
        if (getCharge(stack) < maxCharge) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.getTraits().contains(trait)) {
                if (maxCharge - 10 <= getCharge(stack)) {
                    playerSoul.setMagic(playerSoul.getMagic() - (maxCharge - getCharge(stack)));
                    setCharge(stack, maxCharge);
                } else {
                    int magic = MathHelper.floor(Math.min(playerSoul.getMagic(), 10f));
                    playerSoul.setMagic(playerSoul.getMagic() - magic);
                    increaseCharge(stack, magic);
                }
                playerSoul.resetLastCastTime();
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.soulforge.charge_tooltip").append(": " + getCharge(stack) + "/" + maxCharge));
    }
}
