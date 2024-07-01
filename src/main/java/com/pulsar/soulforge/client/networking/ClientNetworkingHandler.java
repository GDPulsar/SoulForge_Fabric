package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.components.SoulComponent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetworkingHandler {
    public static SoulComponent playerSoul;

    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(PlayerSoulPacket.ID, PlayerSoulPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(PerformAnimationPacket.ID, PerformAnimationPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(PositionVelocityPacket.ID, PositionVelocityPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SetThirdPersonPacket.ID, SetThirdPersonPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(SetSpokenTextPacket.ID, SetSpokenTextPacket::receive);
    }
}
