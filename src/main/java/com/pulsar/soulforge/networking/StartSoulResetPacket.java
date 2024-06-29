package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Constants;
import com.pulsar.soulforge.util.ResetData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class StartSoulResetPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.canReset()) {
            player.getInventory().removeStack(player.getInventory().indexOf(new ItemStack(SoulForgeItems.DETERMINATION_ARNICITE_HEART)), 1);
            //playerSoul.reset();
            playerSoul.addTag("resettingSoul");
            playerSoul.addTag("immobile");
            PacketByteBuf buffer = PacketByteBufs.create().writeUuid(player.getUuid()).writeString("im_going_to_see_mettaton_brb");
            buffer.writeBoolean(false);
            SoulForgeNetworking.broadcast(null, server, SoulForgeNetworking.PERFORM_ANIMATION, buffer);
        } else {
            int hostileCount = 0;
            for (String monster : playerSoul.getMonsterSouls().keySet()) {
                if (Constants.hostiles.contains(monster)) {
                    hostileCount++;
                }
            }
            if (hostileCount < 7) {
                player.sendMessage(Text.literal("Hostile Monsters: " + hostileCount + "/7"));
            } else if (!player.getInventory().contains(new ItemStack(SoulForgeItems.DETERMINATION_ARNICITE_HEART))) {
                player.sendMessage(Text.literal("No Arnicite Heart!"));
            }
        }
    }
}
