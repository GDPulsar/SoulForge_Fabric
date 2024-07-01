package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.item.SoulForgeItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public record ReloadSelectPacket(ItemStack type) implements CustomPayload {
    public static final CustomPayload.Id<ReloadSelectPacket> ID = new Id<>(SoulForgeNetworking.RELOAD_SELECT);
    public static final PacketCodec<RegistryByteBuf, ReloadSelectPacket> CODEC = PacketCodec.tuple(
            ItemStack.PACKET_CODEC, ReloadSelectPacket::type,
            ReloadSelectPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(ReloadSelectPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        ItemStack type = packet.type();
        ItemStack held = player.getMainHandStack();
        String typeStr = null;
        if (type.getItem() == SoulForgeItems.FROSTBITE_ROUND) typeStr = "frostbite";
        else if (type.getItem() == SoulForgeItems.CRUSHING_ROUND) typeStr = "crushing";
        else if (type.getItem() == SoulForgeItems.PUNCTURING_ROUND) typeStr = "puncturing";
        else if (type.getItem() == SoulForgeItems.SUPPRESSING_ROUND) typeStr = "suppressing";
        held.set(SoulForgeItems.RELOAD_COMPONENT, typeStr);
    }
}
