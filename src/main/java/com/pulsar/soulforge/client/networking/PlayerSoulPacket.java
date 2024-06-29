package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.PlayerSoulComponent;
import com.pulsar.soulforge.util.ResetData;
import io.netty.handler.codec.DecoderException;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class PlayerSoulPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int slot = 0;
        if (ClientNetworkingHandler.playerSoul != null) {
            slot = ClientNetworkingHandler.playerSoul.getAbilitySlot();
        }
        try {
            ClientNetworkingHandler.playerSoul = PlayerSoulComponent.fromBuffer(client.player, buf);
        } catch (DecoderException e) {
            SoulForge.LOGGER.warn("Exception occurred while receiving player soul data. Exception: " + e);
        }
        ClientNetworkingHandler.playerSoul.setAbilitySlot(slot);
    }
}
