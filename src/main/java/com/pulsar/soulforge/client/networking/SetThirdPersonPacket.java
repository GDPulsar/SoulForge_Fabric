package com.pulsar.soulforge.client.networking;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.Perspective;
import net.minecraft.network.PacketByteBuf;

public class SetThirdPersonPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        client.options.setPerspective(buf.readBoolean() ? Perspective.THIRD_PERSON_BACK : Perspective.FIRST_PERSON);
    }
}
