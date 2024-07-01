package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.animation.ISoulForgeAnimatedPlayer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SetSpokenTextPacket(String text, Integer speed, Integer timeToDisappear) implements CustomPayload {
    public static final CustomPayload.Id<SetSpokenTextPacket> ID = new Id<>(SoulForgeNetworking.SET_SPOKEN_TEXT);
    public static final PacketCodec<RegistryByteBuf, SetSpokenTextPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SetSpokenTextPacket::text,
            PacketCodecs.INTEGER, SetSpokenTextPacket::speed,
            PacketCodecs.INTEGER, SetSpokenTextPacket::timeToDisappear,
            SetSpokenTextPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SetSpokenTextPacket packet, ClientPlayNetworking.Context context) {
        PlayerEntity player = context.player();
        if (player != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            String text = packet.text();
            playerSoul.setSpokenText(text, packet.speed(), packet.timeToDisappear());
        }
    }
}
