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

public record SwitchModePacket(Integer direction) implements CustomPayload {
    public static final CustomPayload.Id<SwitchModePacket> ID = new Id<>(SoulForgeNetworking.SWITCH_MODE);
    public static final PacketCodec<RegistryByteBuf, SwitchModePacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, SwitchModePacket::direction,
            SwitchModePacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SwitchModePacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        int direction = packet.direction();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (direction > 0) playerSoul.setAbilityRow((playerSoul.getAbilityRow()+1)%4);
        if (direction < 0) playerSoul.setAbilityRow((playerSoul.getAbilityRow()+3)%4);
    }
}
