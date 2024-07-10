package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.WormholeEntity;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public class SpawnWormholePacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID fromUUID = buf.readUuid();
        UUID toUUID = buf.readUuid();
        PlayerEntity fromPlayer = player.getServerWorld().getPlayerByUuid(fromUUID);
        PlayerEntity toPlayer = player.getWorld().getPlayerByUuid(toUUID);
        if (fromPlayer != null && toPlayer != null) {
            ServerWorld serverWorld = server.getWorld(toPlayer.getWorld().getRegistryKey());
            if (serverWorld != null) {
                SoulComponent targetSoul = SoulForge.getPlayerSoul(toPlayer);
                targetSoul.removeWormholeRequest();
                fromPlayer.teleport(serverWorld, toPlayer.getX(), toPlayer.getY(), toPlayer.getZ(), null, toPlayer.getYaw(), toPlayer.getPitch());
                WormholeEntity wormhole = new WormholeEntity(serverWorld, player.getPos(), serverWorld, toPlayer.getPos());
                wormhole.setPosition(player.getPos());
                serverWorld.spawnEntity(wormhole);
                fromPlayer.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 200, 0));
            }
        }
    }
}
