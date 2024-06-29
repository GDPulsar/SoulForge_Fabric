package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class SwitchModePacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int direction = buf.readVarInt();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (direction > 0) playerSoul.setAbilityRow((playerSoul.getAbilityRow()+1)%4);
        if (direction < 0) playerSoul.setAbilityRow((playerSoul.getAbilityRow()+3)%4);
    }
}
