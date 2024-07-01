package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.devices.ArmorDeviceBase;
import com.pulsar.soulforge.item.devices.DeviceBase;
import com.pulsar.soulforge.item.weapons.JusticeBow;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record CastAbilityPacket(Boolean magicMode, Integer slotNum) implements CustomPayload {
    public static final CustomPayload.Id<CastAbilityPacket> ID = new Id<>(SoulForgeNetworking.CAST_ABILITY);
    public static final PacketCodec<RegistryByteBuf, CastAbilityPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, CastAbilityPacket::magicMode,
            PacketCodecs.INTEGER, CastAbilityPacket::slotNum,
            CastAbilityPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(CastAbilityPacket packet, ServerPlayNetworking.Context context) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(context.player());
        if (packet.magicMode()) {
            int index = packet.slotNum();
            if (index != -1) playerSoul.castAbility(index);
        } else {
            if (context.player().getInventory().selectedSlot == 9) {
                if (playerSoul.getWeapon().isOf(SoulForgeItems.JUSTICE_BOW)) {
                    JusticeBow bow = (JusticeBow)playerSoul.getWeapon().getItem();
                    if (!context.player().isSneaking()) {
                        bow.scatter(context.player());
                    } else {
                        bow.aim(context.player());
                    }
                }
            } else {
                ItemStack heldItem = context.player().getMainHandStack();
                if (heldItem.getItem() instanceof DeviceBase device) {
                    device.tryCharge(context.player());
                }
                if (heldItem.getItem() instanceof ArmorDeviceBase device) {
                    device.tryCharge(context.player());
                }
            }
        }
    }
}
