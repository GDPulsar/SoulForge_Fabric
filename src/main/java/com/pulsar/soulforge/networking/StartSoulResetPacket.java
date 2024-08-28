package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.ValueComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.SoulJarItem;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class StartSoulResetPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        boolean holdingJar = player.getMainHandStack().isOf(SoulForgeItems.SOUL_JAR);
        if (playerSoul.canReset() || (holdingJar && SoulJarItem.getHasSoul(player.getMainHandStack()))) {
            if (!holdingJar || !SoulJarItem.getHasSoul(player.getMainHandStack())) {
                player.getInventory().removeStack(player.getInventory().indexOf(new ItemStack(SoulForgeItems.DETERMINATION_ARNICITE_HEART)), 1);
            }
            if (holdingJar) {
                ValueComponent values = SoulForge.getValues(player);
                if (values != null) {
                    values.modifyExtraVals((nbt) -> {
                        nbt.put("heldJar", player.getMainHandStack().writeNbt(new NbtCompound()));
                    });
                }
                player.getMainHandStack().getOrCreateNbt().putBoolean("usedInReroll", true);
            }
            playerSoul.addTag("resettingSoul");
            SoulForge.getValues(player).setBool("Immobilized", true);
            playerSoul.sync();
            PacketByteBuf buffer = PacketByteBufs.create().writeUuid(player.getUuid()).writeString("im_going_to_see_mettaton_brb");
            buffer.writeBoolean(false);
            SoulForgeNetworking.broadcast(null, server, SoulForgeNetworking.PERFORM_ANIMATION, buffer);
        } else {
            if (!player.getInventory().contains(new ItemStack(SoulForgeItems.DETERMINATION_ARNICITE_HEART))) {
                player.sendMessage(Text.literal("No Arnicite Heart!"));
            }
        }
    }
}
