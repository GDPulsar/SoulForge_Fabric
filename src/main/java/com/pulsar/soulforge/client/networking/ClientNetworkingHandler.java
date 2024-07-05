package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetworkingHandler {
    public static SoulComponent playerSoul;

    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.PLAYER_SOUL, PlayerSoulPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.USE_MAGIC, UseMagicPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.PERFORM_ANIMATION, PerformAnimationPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.POSITION_VELOCITY, PositionVelocityPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SoulForgeNetworking.SET_THIRD_PERSON, SetThirdPersonPacket::receive);
    }
}
