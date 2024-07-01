package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.UUID;

public record PositionVelocityPacket(Vector3f position, Vector3f velocity, Vector3f addVelocity) implements CustomPayload {
    public static final CustomPayload.Id<PositionVelocityPacket> ID = new Id<>(SoulForgeNetworking.POSITION_VELOCITY);
    public static final PacketCodec<RegistryByteBuf, PositionVelocityPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR3F, PositionVelocityPacket::position,
            PacketCodecs.VECTOR3F, PositionVelocityPacket::velocity,
            PacketCodecs.VECTOR3F, PositionVelocityPacket::addVelocity,
            PositionVelocityPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(PositionVelocityPacket packet, ClientPlayNetworking.Context context) {
        if (!Objects.equals(packet.position(), new Vector3f())) {
            Vec3d position = Utils.vector3fToVec3d(packet.position());
            context.player().setPosition(position);
        }
        if (!Objects.equals(packet.velocity(), new Vector3f())) {
            Vec3d velocity = Utils.vector3fToVec3d(packet.velocity());
            context.player().setVelocity(velocity);
        }
        if (!Objects.equals(packet.addVelocity(), new Vector3f())) {
            Vec3d addVelocity = Utils.vector3fToVec3d(packet.addVelocity());
            context.player().addVelocityInternal(addVelocity);
        }
    }
}
