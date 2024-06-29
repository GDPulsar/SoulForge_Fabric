package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.client.ui.DetermineScreen;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class DetermineScreenPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int currentTrait = buf.readVarInt();
        client.execute(() -> client.setScreen(new DetermineScreen(currentTrait)));
    }
}
