package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.accessors.HasTickManager;
import com.pulsar.soulforge.util.TickManager;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class UpdateTickRatePacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (client.world != null) {
            TickManager manager = ((HasTickManager)client.world).getTickManager();
            manager.setTickRate(buf.readVarInt());
            manager.setFrozen(buf.readBoolean());
        }
    }
}
