package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.networking.SoulForgeNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.Perspective;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SetThirdPersonPacket(Boolean thirdPerson) implements CustomPayload {
    public static final CustomPayload.Id<SetThirdPersonPacket> ID = new Id<>(SoulForgeNetworking.SET_THIRD_PERSON);
    public static final PacketCodec<RegistryByteBuf, SetThirdPersonPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, SetThirdPersonPacket::thirdPerson,
            SetThirdPersonPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SetThirdPersonPacket packet, ClientPlayNetworking.Context context) {
        context.client().options.setPerspective(packet.thirdPerson() ? Perspective.THIRD_PERSON_BACK : Perspective.FIRST_PERSON);
    }
}
