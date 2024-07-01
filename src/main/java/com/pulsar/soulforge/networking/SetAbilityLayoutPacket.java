package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
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

public record SetAbilityLayoutPacket(String name, Integer row, Integer column) implements CustomPayload {
    public static final CustomPayload.Id<SetAbilityLayoutPacket> ID = new Id<>(SoulForgeNetworking.SET_ABILITY_LAYOUT);
    public static final PacketCodec<RegistryByteBuf, SetAbilityLayoutPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SetAbilityLayoutPacket::name,
            PacketCodecs.INTEGER, SetAbilityLayoutPacket::row,
            PacketCodecs.INTEGER, SetAbilityLayoutPacket::column,
            SetAbilityLayoutPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SetAbilityLayoutPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        String name = packet.name();
        int row = packet.row();
        int column = packet.column();
        if (name.equals("null")) {
            playerSoul.setLayoutAbility(null, row, column);
            return;
        }
        for (AbilityBase ability : playerSoul.getAbilities()) {
            if (ability.getID().toString().equals(name)) {
                playerSoul.setLayoutAbility(ability, row, column);
                return;
            }
        }
    }
}
