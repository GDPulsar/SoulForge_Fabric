package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class RampageActivatePacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        int rampageStart = buf.readVarInt();
        int rampageActive = buf.readVarInt();
        int rampageEnd = buf.readVarInt();
        playerSoul.setValue("rampageStart", rampageStart);
        playerSoul.setValue("rampageActive", rampageActive);
        playerSoul.setValue("rampageEnd", rampageEnd);
        playerSoul.setValue("rampageTimer", 0);
        playerSoul.addTag("rampaging");
    }
}
