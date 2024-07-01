package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.components.AbilityList;
import com.pulsar.soulforge.components.PlayerSoulComponent;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.util.ResetData;
import io.netty.handler.codec.DecoderException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record PlayerSoulPacket(Integer exp, Float magic, NbtCompound abilityLayout, Integer abilityRow, Boolean magicMode) implements CustomPayload {
    public static final CustomPayload.Id<PlayerSoulPacket> ID = new Id<>(SoulForgeNetworking.PLAYER_SOUL);
    public static final PacketCodec<RegistryByteBuf, PlayerSoulPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, PlayerSoulPacket::exp,
            PacketCodecs.FLOAT, PlayerSoulPacket::magic,
            PacketCodecs.NBT_COMPOUND, PlayerSoulPacket::abilityLayout,
            PacketCodecs.INTEGER, PlayerSoulPacket::abilityRow,
            PacketCodecs.BOOL, PlayerSoulPacket::magicMode,
            PlayerSoulPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(PlayerSoulPacket packet, ClientPlayNetworking.Context context) {
        SoulForge.getPlayerSoul(context.player()).fromPacket(packet);
    }
}
