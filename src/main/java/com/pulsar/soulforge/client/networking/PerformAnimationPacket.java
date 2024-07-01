package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.animation.ISoulForgeAnimatedPlayer;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record PerformAnimationPacket(UUID playerUUID, String animationName, boolean firstPerson) implements CustomPayload {
    public static final CustomPayload.Id<PerformAnimationPacket> ID = new Id<>(SoulForgeNetworking.PERFORM_ANIMATION);
    public static final PacketCodec<RegistryByteBuf, PerformAnimationPacket> CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, PerformAnimationPacket::playerUUID,
            PacketCodecs.STRING, PerformAnimationPacket::animationName,
            PacketCodecs.BOOL, PerformAnimationPacket::firstPerson,
            PerformAnimationPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(PerformAnimationPacket packet, ClientPlayNetworking.Context context) {
        PlayerEntity player = context.player().getWorld().getPlayerByUuid(packet.playerUUID());
        String animationName = packet.animationName();
        boolean firstPerson = packet.firstPerson();
        if (player != null) {
            var animationContainer = ((ISoulForgeAnimatedPlayer) player).soulforge_getModAnimation();

            KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(Identifier.of(SoulForge.MOD_ID, animationName));
            KeyframeAnimationPlayer animationPlayer = new KeyframeAnimationPlayer(anim);
            if (firstPerson) animationPlayer = animationPlayer
                    .setFirstPersonConfiguration(new FirstPersonConfiguration().setShowLeftArm(true).setShowRightArm(true))
                    .setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL);
            if (animationContainer.isActive()) animationContainer.replaceAnimationWithFade(
                    AbstractFadeModifier.functionalFadeIn(10, (modelName, type, value) -> Ease.INOUTSINE.invoke(value)), animationPlayer);
            else animationContainer.setAnimation(animationPlayer);
        }
    }
}
