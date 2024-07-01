package com.pulsar.soulforge.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class ArniciteGeode extends Item {
    public ArniciteGeode() {
        super(new Item.Settings());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack geode = user.getStackInHand(hand);
        if (!world.isClient) {
            geode.decrement(1);
            int arniciteCount = MathHelper.floor(Math.random() * 2 + 2);
            for (int i = 0; i < arniciteCount; i++) {
                ItemStack item;
                double chance = Math.random();
                if (chance <= 0.2f) {
                    item = new ItemStack(SoulForgeItems.ARNICITE);
                } else if (chance <= 0.86f) {
                    item = new ItemStack(getRandomArnicite());
                } else {
                    item = new ItemStack(SoulForgeItems.DETERMINATION_ARNICITE);
                }
                if (!user.giveItemStack(item)) {
                    user.dropItem(item, true);
                }
            }
            world.playSoundFromEntity(null, user, SoundEvents.BLOCK_POINTED_DRIPSTONE_FALL, SoundCategory.PLAYERS, 1f, 1f);
        }
        return TypedActionResult.consume(geode);
    }

    public Item getRandomArnicite() {
        Random rnd = new Random();
        return switch (rnd.nextInt(6)) {
            case 0 -> SoulForgeItems.BRAVERY_ARNICITE;
            case 1 -> SoulForgeItems.JUSTICE_ARNICITE;
            case 2 -> SoulForgeItems.KINDNESS_ARNICITE;
            case 3 -> SoulForgeItems.INTEGRITY_ARNICITE;
            case 4 -> SoulForgeItems.PATIENCE_ARNICITE;
            case 5 -> SoulForgeItems.PERSEVERANCE_ARNICITE;
            default -> SoulForgeItems.BRAVERY_ARNICITE;
        };
    }
}
