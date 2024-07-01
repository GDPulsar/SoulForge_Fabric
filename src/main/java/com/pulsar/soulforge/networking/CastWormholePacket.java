package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record CastWormholePacket(UUID targetPlayer) implements CustomPayload {
    public static final CustomPayload.Id<CastWormholePacket> ID = new Id<>(SoulForgeNetworking.CAST_WORMHOLE);
    public static final PacketCodec<RegistryByteBuf, CastWormholePacket> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, CastWormholePacket::targetPlayer,
            CastWormholePacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CastWormholePacket packet, ServerPlayNetworking.Context context) {
        UUID targetUUID = packet.targetPlayer();
        for (ServerPlayerEntity target : context.server().getPlayerManager().getPlayerList()) {
            if (target.getUuid().compareTo(targetUUID) == 0) {
                SoulComponent targetSoul = SoulForge.getPlayerSoul(target);
                targetSoul.createWormholeRequest(context.player());
                MutableText text = Text.literal("Teleport request from " + context.player().getName().getString() + ". Accept?");
                text.setStyle(Style.EMPTY.withColor(Formatting.AQUA).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/acceptwormhole")));
                target.sendMessage(text);
                break;
            }
        }
    }
}
