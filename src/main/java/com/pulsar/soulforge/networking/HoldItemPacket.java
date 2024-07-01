package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.entity.DeterminationArrowProjectile;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.weapons.BFRCMG;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.event.GameEvent;

import java.awt.*;

public record HoldItemPacket(boolean mouseDown) implements CustomPayload {
    public static final CustomPayload.Id<HoldItemPacket> ID = new Id<>(SoulForgeNetworking.HOLD_ITEM);
    public static final PacketCodec<RegistryByteBuf, HoldItemPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, HoldItemPacket::mouseDown,
            HoldItemPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(HoldItemPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        boolean mouseDown = packet.mouseDown();
        ItemStack stack = player.getMainHandStack();
        if (stack != null && !stack.isEmpty()) {
            if (stack.isOf(SoulForgeItems.BFRCMG)) {
                stack.set(SoulForgeItems.ACTIVE_COMPONENT, mouseDown);
            }
            if (stack.isIn(SoulForgeTags.IMBUER_AXES)) {
                if (Utils.isImbued(stack, player)) {
                    stack.set(SoulForgeItems.MOUSE_DOWN_COMPONENT, mouseDown);
                }
            }
        }
    }
}
