package com.pulsar.soulforge.item;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.traits.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ArniciteItem extends Item {
    public ArniciteItem() {
        super(new Item.Settings());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            SoulComponent playerSoul;
            if (user instanceof ServerPlayerEntity) playerSoul = SoulForge.getPlayerSoul(user);
            else playerSoul = SoulForgeClient.getPlayerData();
            if (playerSoul.getMagic() < 50) return TypedActionResult.fail(user.getStackInHand(hand));
            TraitBase trait;
            if (playerSoul.getTraitCount() >= 2) trait = playerSoul.getTrait(MathHelper.floor(Math.random() * 2));
            else trait = playerSoul.getTrait(0);
            ItemStack result = ItemStack.EMPTY;
            if (trait instanceof Determination) result = new ItemStack(SoulForgeItems.DETERMINATION_ARNICITE);
            if (trait instanceof Bravery) result = new ItemStack(SoulForgeItems.BRAVERY_ARNICITE);
            if (trait instanceof Justice) result = new ItemStack(SoulForgeItems.JUSTICE_ARNICITE);
            if (trait instanceof Kindness) result = new ItemStack(SoulForgeItems.KINDNESS_ARNICITE);
            if (trait instanceof Patience) result = new ItemStack(SoulForgeItems.PATIENCE_ARNICITE);
            if (trait instanceof Perseverance) result = new ItemStack(SoulForgeItems.PERSEVERANCE_ARNICITE);
            if (trait instanceof Integrity) result = new ItemStack(SoulForgeItems.INTEGRITY_ARNICITE);
            user.giveItemStack(result);
            playerSoul.setMagic(playerSoul.getMagic() - 50f);
            playerSoul.resetLastCastTime();
            user.getStackInHand(hand).decrement(1);
        }
        return TypedActionResult.consume(user.getStackInHand(hand));
    }
}
