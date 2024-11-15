package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import io.netty.handler.codec.DecoderException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

import java.util.function.Consumer;

public class ClientNetworkingHandler {
    public static SoulComponent playerSoul;

    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.PLAYER_SOUL, PlayerSoulPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.USE_MAGIC, UseMagicPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.PERFORM_ANIMATION, PerformAnimationPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.POSITION_VELOCITY, PositionVelocityPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.SET_THIRD_PERSON, SetThirdPersonPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.UPDATE_TICK_RATE, UpdateTickRatePacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.SHOW_TOAST, ShowToastPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.OPEN_SCREEN, OpenScreenPacket::receive);
    }

    private static void soulPacketReceiver(MinecraftClient client, Consumer<SoulComponent> receiver) {
        if (client.player == null) return;
        try {
            if (ClientNetworkingHandler.playerSoul == null) ClientNetworkingHandler.playerSoul = new SoulComponent(client.player);
            receiver.accept(ClientNetworkingHandler.playerSoul);
        } catch (DecoderException e) {
            SoulForge.LOGGER.warn("Exception occurred while receiving soul data. Exception: {}", String.valueOf(e));
        }
    }
}
