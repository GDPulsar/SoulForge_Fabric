package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.networking.PerformAnimationPacket;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Constants;
import com.pulsar.soulforge.util.ResetData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public record StartSoulResetPacket() implements CustomPayload {
    public static final CustomPayload.Id<StartSoulResetPacket> ID = new Id<>(SoulForgeNetworking.START_SOUL_RESET);
    public static final PacketCodec<RegistryByteBuf, StartSoulResetPacket> CODEC = new PacketCodec<>() {
        @Override
        public StartSoulResetPacket decode(RegistryByteBuf buf) {
            return new StartSoulResetPacket();
        }

        @Override
        public void encode(RegistryByteBuf buf, StartSoulResetPacket value) {}
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(StartSoulResetPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.canReset()) {
            player.getInventory().removeStack(player.getInventory().indexOf(new ItemStack(SoulForgeItems.DETERMINATION_ARNICITE_HEART)), 1);
            //playerSoul.reset();
            playerSoul.addTag("resettingSoul");
            playerSoul.addTag("immobile");
            SoulForgeNetworking.broadcast(null, context.server(), new PerformAnimationPacket(player.getUuid(), "im_going_to_see_mettaton_brb", false));
        } else {
            if (!player.getInventory().contains(new ItemStack(SoulForgeItems.DETERMINATION_ARNICITE_HEART))) {
                player.sendMessage(Text.literal("No Arnicite Heart!"));
            }
        }
    }
}
