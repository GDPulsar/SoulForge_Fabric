package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.ValueComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class RampageActivatePacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ValueComponent values = SoulForge.getValues(player);
        if (values != null) {
            int rampageStart = buf.readVarInt();
            int rampageActive = buf.readVarInt();
            int rampageEnd = buf.readVarInt();
            values.setInt("rampageStart", rampageStart);
            values.setInt("rampageActive", rampageActive);
            values.setInt("rampageEnd", rampageEnd);
            values.setInt("rampageTimer", 0);
            values.setBool("rampaging", true);
        }
    }
}
