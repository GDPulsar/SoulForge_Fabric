package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class SetWeaponPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ItemStack stack = buf.readItemStack();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.setWeapon(stack);
        if (stack.isOf(SoulForgeItems.JUSTICE_REVOLVER) || stack.isOf(SoulForgeItems.GUNBLADES)) {
            stack.getOrCreateNbt().putInt("ammo", playerSoul.getLV() + 6);
        }
        if (stack.isOf(SoulForgeItems.DETERMINATION_GUN)) {
            stack.getOrCreateNbt().putInt("ammo", playerSoul.getEffectiveLV() + 6);
        }
    }
}
