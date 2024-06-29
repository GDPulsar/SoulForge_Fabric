package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.SwordSlashProjectile;
import com.pulsar.soulforge.item.devices.machines.SiphonImbuer;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;

public class RightClickPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        ItemStack heldItem = player.getMainHandStack();
        if (heldItem != null) {
            if (heldItem.isIn(SoulForgeTags.IMBUER_SWORDS) && !player.getItemCooldownManager().isCoolingDown(heldItem.getItem())) {
                if (heldItem.getNbt() != null) {
                    if (heldItem.getNbt().getBoolean("imbued")) {
                        ItemStack imbuerStack = Utils.getImbuer(heldItem, player);
                        if (imbuerStack != null) {
                            if (((SiphonImbuer)imbuerStack.getItem()).getCharge(imbuerStack) >= 5) {
                                ((SiphonImbuer)imbuerStack.getItem()).decreaseCharge(imbuerStack, 5);
                                SwordSlashProjectile slash = new SwordSlashProjectile(player.getWorld(), player, (float)(player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)*1.5f));
                                slash.setPosition(Utils.getArmPosition(player));
                                slash.setVelocity(player.getRotationVector().withAxis(Direction.Axis.Y, 0).multiply(1.5f));
                                player.getWorld().spawnEntity(slash);
                                player.getServerWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1f, 1f);
                                player.getItemCooldownManager().set(heldItem.getItem(), 10);
                            }
                        }
                    }
                }
            }
        }
    }
}
