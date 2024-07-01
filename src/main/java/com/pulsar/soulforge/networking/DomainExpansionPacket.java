package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.weapons.Gunblades;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record DomainExpansionPacket() implements CustomPayload {
    public static final CustomPayload.Id<DomainExpansionPacket> ID = new Id<>(SoulForgeNetworking.DOMAIN_EXPANSION);
    public static final PacketCodec<RegistryByteBuf, DomainExpansionPacket> CODEC = new PacketCodec<>() {
        @Override
        public DomainExpansionPacket decode(RegistryByteBuf buf) {
            return new DomainExpansionPacket();
        }

        @Override
        public void encode(RegistryByteBuf buf, DomainExpansionPacket value) {}
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(DomainExpansionPacket packet, ServerPlayNetworking.Context context) {
        context.player().getWorld().playSound(null, context.player().getBlockPos(), SoulForgeSounds.DOMAIN_EXPANSION_EVENT, SoundCategory.MASTER, 10f, 1f);
    }
}
