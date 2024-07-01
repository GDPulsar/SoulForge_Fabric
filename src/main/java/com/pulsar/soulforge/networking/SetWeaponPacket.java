package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.weapons.Gunblades;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public record SetWeaponPacket(ItemStack weapon) implements CustomPayload {
    public static final CustomPayload.Id<SetWeaponPacket> ID = new Id<>(SoulForgeNetworking.SET_WEAPON);
    public static final PacketCodec<RegistryByteBuf, SetWeaponPacket> CODEC = PacketCodec.tuple(
            ItemStack.PACKET_CODEC, SetWeaponPacket::weapon,
            SetWeaponPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SetWeaponPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        ItemStack stack = packet.weapon();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.setWeapon(stack);
        if (stack.isOf(SoulForgeItems.GUNBLADES)) {
            ((Gunblades)stack.getItem()).ammo = playerSoul.getLV() + 6;
        }
    }
}
