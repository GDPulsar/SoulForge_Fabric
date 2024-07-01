package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.entity.SwordSlashProjectile;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.devices.machines.SiphonImbuer;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;

public record RightClickPacket() implements CustomPayload {
    public static final CustomPayload.Id<RightClickPacket> ID = new Id<>(SoulForgeNetworking.RIGHT_CLICK);
    public static final PacketCodec<RegistryByteBuf, RightClickPacket> CODEC = new PacketCodec<>() {
        @Override
        public RightClickPacket decode(RegistryByteBuf buf) {
            return new RightClickPacket();
        }

        @Override
        public void encode(RegistryByteBuf buf, RightClickPacket value) {}
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(RightClickPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        ItemStack heldItem = player.getMainHandStack();
        if (heldItem != null) {
            if (heldItem.isIn(SoulForgeTags.IMBUER_SWORDS) && !player.getItemCooldownManager().isCoolingDown(heldItem.getItem())) {
                if (Boolean.TRUE.equals(heldItem.get(SoulForgeItems.IMBUED_COMPONENT))) {
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
