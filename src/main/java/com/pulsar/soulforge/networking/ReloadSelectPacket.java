package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.Abilities;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.pures.Determine;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ReloadSelectPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ItemStack type = buf.readItemStack();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        ItemStack held = player.getMainHandStack();
        String typeStr = null;
        if (type.getItem() == SoulForgeItems.FROSTBITE_ROUND) typeStr = "frostbite";
        else if (type.getItem() == SoulForgeItems.CRUSHING_ROUND) typeStr = "crushing";
        else if (type.getItem() == SoulForgeItems.PUNCTURING_ROUND) typeStr = "puncturing";
        else if (type.getItem() == SoulForgeItems.SUPPRESSING_ROUND) typeStr = "suppressing";
        if (held.isOf(Items.CROSSBOW)) {
            loadCrossbow(held, type, typeStr);
        }
        if (held.isOf(Items.BOW)) {
            loadOther(held, typeStr);
        }
        if (held.isOf(SoulForgeItems.JUSTICE_GUN) || held.isOf(SoulForgeItems.JUSTICE_REVOLVER)) {
            loadArniciteGun(held, typeStr);
        }
    }

    public static void loadCrossbow(ItemStack crossbow, ItemStack round, String type) {
        NbtCompound itemNbt = crossbow.getOrCreateNbt();
        NbtList list = new NbtList();
        if (itemNbt.contains("ChargedProjectiles", 9)) {
            list = itemNbt.getList("ChargedProjectiles", 10);
        }
        NbtCompound nbt = new NbtCompound();
        round.writeNbt(nbt);
        list.add(nbt);
        itemNbt.put("ChargedProjectiles", list);
        itemNbt.putBoolean("Charged", true);
        itemNbt.putString("reloaded", type);
    }

    public static void loadArniciteGun(ItemStack item, String type) {
        NbtCompound nbt = item.getOrCreateNbt();
        nbt.putInt("reloadedCount", 6);
        nbt.putString("reloaded", type);
    }

    public static void loadOther(ItemStack item, String type) {
        NbtCompound nbt = item.getOrCreateNbt();
        nbt.putString("reloaded", type);
    }
}
