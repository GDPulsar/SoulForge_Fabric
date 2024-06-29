package com.pulsar.soulforge.item;

import com.pulsar.soulforge.client.ui.EncyclopediaScreen;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EncyclopediaItem extends Item {
    public EncyclopediaItem() {
        super(new FabricItemSettings().maxCount(1));
    }
}
