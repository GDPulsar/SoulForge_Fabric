package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.entity.WormholeEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record SpawnWormholePacket(UUID from, UUID to) implements CustomPayload {
    public static final CustomPayload.Id<SpawnWormholePacket> ID = new Id<>(SoulForgeNetworking.SPAWN_WORMHOLE);
    public static final PacketCodec<RegistryByteBuf, SpawnWormholePacket> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, SpawnWormholePacket::from,
            Uuids.PACKET_CODEC, SpawnWormholePacket::to,
            SpawnWormholePacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SpawnWormholePacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        UUID fromUUID = packet.from();
        UUID toUUID = packet.to();
        PlayerEntity fromPlayer = player.getServerWorld().getPlayerByUuid(fromUUID);
        PlayerEntity toPlayer = player.getWorld().getPlayerByUuid(toUUID);
        if (fromPlayer != null && toPlayer != null) {
            ServerWorld serverWorld = context.server().getWorld(toPlayer.getWorld().getRegistryKey());
            if (serverWorld != null) {
                fromPlayer.teleport(serverWorld, toPlayer.getX(), toPlayer.getY(), toPlayer.getZ(), null, toPlayer.getYaw(), toPlayer.getPitch());
                WormholeEntity wormhole = new WormholeEntity(serverWorld, player.getPos(), serverWorld, toPlayer.getPos());
                wormhole.setPosition(player.getPos());
                serverWorld.spawnEntity(wormhole);
            }
        }
    }
}
