package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.weapons.Gunblades;
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
        if (stack.isOf(SoulForgeItems.GUNBLADES)) {
            ((Gunblades)stack.getItem()).ammo = playerSoul.getLV() + 6;
        }
    }
}
