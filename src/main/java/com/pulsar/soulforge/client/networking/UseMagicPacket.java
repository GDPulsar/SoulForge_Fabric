package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.SoulForgeClient;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class UseMagicPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        SoulForgeClient.getPlayerData().setMagic(SoulForgeClient.getPlayerData().getMagic() - buf.readVarInt());
    }
}
