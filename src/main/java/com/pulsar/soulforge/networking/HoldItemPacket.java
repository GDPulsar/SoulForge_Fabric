package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class HoldItemPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        boolean mouseDown = buf.readBoolean();
        ItemStack stack = player.getMainHandStack();
        if (stack != null && !stack.isEmpty()) {
            if (stack.isOf(SoulForgeItems.BFRCMG)) {
                stack.getOrCreateNbt();
                stack.getNbt().putBoolean("active", mouseDown);
            }
            if (stack.isIn(SoulForgeTags.IMBUER_AXES)) {
                if (Utils.isImbued(stack, player)) {
                    stack.getOrCreateNbt();
                    stack.getNbt().putBoolean("mouseDown", mouseDown);
                }
            }
        }
    }
}
