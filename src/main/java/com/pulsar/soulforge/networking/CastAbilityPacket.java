package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.devices.ArmorDeviceBase;
import com.pulsar.soulforge.item.devices.DeviceBase;
import com.pulsar.soulforge.item.devices.PickaxeDeviceBase;
import com.pulsar.soulforge.item.weapons.JusticeBow;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class CastAbilityPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (buf.readBoolean()) {
            boolean isName = buf.readBoolean();
            if (isName) {
                String name = buf.readString();
                playerSoul.castAbility(playerSoul.getAbility(name));
            } else {
                int index = buf.readVarInt();
                if (index != -1) playerSoul.castAbility(index);
            }
        } else {
            if (player.getInventory().selectedSlot == 9) {
                if (playerSoul.getWeapon().isOf(SoulForgeItems.JUSTICE_BOW)) {
                    JusticeBow bow = (JusticeBow)playerSoul.getWeapon().getItem();
                    if (!player.isSneaking()) {
                        bow.scatter(player);
                    } else {
                        bow.aim(player);
                    }
                }
            } else {
                ItemStack heldItem = player.getMainHandStack();
                if (heldItem.getItem() instanceof DeviceBase device) {
                    device.tryCharge(player);
                }
                if (heldItem.getItem() instanceof ArmorDeviceBase device) {
                    device.tryCharge(player);
                }
                if (heldItem.getItem() instanceof PickaxeDeviceBase device) {
                    device.tryCharge(player);
                }
            }
        }
    }
}
