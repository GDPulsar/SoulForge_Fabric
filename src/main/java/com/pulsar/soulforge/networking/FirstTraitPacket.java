package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Random;

public class FirstTraitPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        TraitBase trait = List.of(Traits.bravery, Traits.justice, Traits.kindness, Traits.patience, Traits.integrity, Traits.perseverance).get(buf.readVarInt());
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (!playerSoul.getResetData().setFirst) {
            Random random = new Random();
            int val = random.nextInt(50);
            if (val >= 45) {
                TraitBase trait2 = List.of(Traits.bravery, Traits.justice, Traits.kindness, Traits.patience, Traits.integrity, Traits.perseverance).get(random.nextInt(6));
                while (trait2 == trait) trait2 = List.of(Traits.bravery, Traits.justice, Traits.kindness, Traits.patience, Traits.integrity, Traits.perseverance).get(random.nextInt(6));
                playerSoul.setTraits(List.of(trait, trait2));
                if (val == 45) playerSoul.setStrong(true);
            } else {
                if (val == 0) {
                    playerSoul.setPure(true);
                } else if (val <= 5) {
                    playerSoul.setStrong(true);
                }
                playerSoul.setTraits(List.of(trait));
            }
            playerSoul.getResetData().setFirst = true;
        }
    }
}
