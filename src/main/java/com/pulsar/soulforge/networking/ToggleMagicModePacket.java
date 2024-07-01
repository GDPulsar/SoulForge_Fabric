package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public record ToggleMagicModePacket(boolean state) implements CustomPayload {
    public static final CustomPayload.Id<ToggleMagicModePacket> ID = new Id<>(SoulForgeNetworking.TOGGLE_MAGIC_MODE);
    public static final PacketCodec<RegistryByteBuf, ToggleMagicModePacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, ToggleMagicModePacket::state,
            ToggleMagicModePacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(ToggleMagicModePacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.magicModeActive() != packet.state()) playerSoul.toggleMagicMode();
    }
}
