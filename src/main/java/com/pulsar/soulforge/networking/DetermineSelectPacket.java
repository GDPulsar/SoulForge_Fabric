package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.Abilities;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.pures.Determine;
import com.pulsar.soulforge.components.SoulComponent;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class DetermineSelectPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Identifier abilityID = buf.readIdentifier();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        for (AbilityBase ability : playerSoul.getAbilities()) {
            if (ability instanceof Determine determine) {
                determine.end(player);
                determine.selected = Abilities.get(abilityID).getInstance();
                break;
            }
        }
    }
}
