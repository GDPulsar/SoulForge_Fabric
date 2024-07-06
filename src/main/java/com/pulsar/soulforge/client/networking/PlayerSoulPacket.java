package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.PlayerSoulComponent;
import io.netty.handler.codec.DecoderException;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class PlayerSoulPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (client.player == null) return;
        try {
            if (ClientNetworkingHandler.playerSoul == null) ClientNetworkingHandler.playerSoul = new PlayerSoulComponent(client.player);
            ClientNetworkingHandler.playerSoul.fromBuffer(buf);
        } catch (DecoderException e) {
            SoulForge.LOGGER.warn("Exception occurred while receiving player soul data. Exception: " + e);
        }
    }
}
