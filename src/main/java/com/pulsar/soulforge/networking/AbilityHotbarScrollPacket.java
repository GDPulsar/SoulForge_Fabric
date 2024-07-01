package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record AbilityHotbarScrollPacket(Integer slot) implements CustomPayload {
    public static final CustomPayload.Id<AbilityHotbarScrollPacket> ID = new Id<>(SoulForgeNetworking.ABILITY_HOTBAR_SCROLL);
    public static final PacketCodec<RegistryByteBuf, AbilityHotbarScrollPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, AbilityHotbarScrollPacket::slot,
            AbilityHotbarScrollPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(AbilityHotbarScrollPacket packet, ServerPlayNetworking.Context context) {
        int slot = packet.slot();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(context.player());
        playerSoul.setAbilitySlot(slot);
    }
}
