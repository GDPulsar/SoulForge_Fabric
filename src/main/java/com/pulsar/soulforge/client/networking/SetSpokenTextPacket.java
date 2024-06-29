package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.animation.ISoulForgeAnimatedPlayer;
import com.pulsar.soulforge.components.SoulComponent;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SetSpokenTextPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        PlayerEntity player = client.player;
        if (player != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul != null) {
                String text = buf.readString();
                playerSoul.setSpokenText(text, buf.readVarInt(), buf.readVarInt());
            }
        }
    }
}
