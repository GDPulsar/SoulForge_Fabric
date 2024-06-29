package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class PositionVelocityPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (client.player != null) {
            if (buf.readBoolean()) {
                Vec3d position = Utils.vector3fToVec3d(buf.readVector3f());
                client.player.setPosition(position);
            }
            if (buf.readBoolean()) {
                Vec3d velocity = Utils.vector3fToVec3d(buf.readVector3f());
                client.player.setVelocity(velocity);
            }
            if (buf.readBoolean()) {
                Vec3d addVelocity = Utils.vector3fToVec3d(buf.readVector3f());
                client.player.addVelocity(addVelocity);
            }
        }
    }
}
