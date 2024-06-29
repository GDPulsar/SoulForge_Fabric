package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class SetAbilityLayoutPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        String name = buf.readString();
        int row = buf.readVarInt();
        int column = buf.readVarInt();
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
