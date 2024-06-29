package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class CastWormholePacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID targetUUID = buf.readUuid();
        for (ServerPlayerEntity target : server.getPlayerManager().getPlayerList()) {
            if (target.getUuid().compareTo(targetUUID) == 0) {
                SoulComponent targetSoul = SoulForge.getPlayerSoul(target);
                targetSoul.createWormholeRequest(player);
                MutableText text = Text.literal("Teleport request from " + player.getName().getString() + ". Accept?");
                text.setStyle(Style.EMPTY.withColor(Formatting.AQUA).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/acceptwormhole")));
                target.sendMessage(text);
                break;
            }
        }
    }
}
