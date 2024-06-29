package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class ResetPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.canReset()) {
            int index = -1;
            for (int i = 0; i < player.getInventory().size(); i++) {
                if (player.getInventory().getStack(i).getItem() == SoulForgeItems.DETERMINATION_ARNICITE_HEART) {
                    index = i;
                    break;
                }
            }
            if (index != -1) player.getInventory().removeStack(index, 1);
            playerSoul.reset();
            player.kill();
        }
    }
}
