package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.animation.ISoulForgeAnimatedPlayer;
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
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class PerformAnimationPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        PlayerEntity player = client.world.getPlayerByUuid(buf.readUuid());
        String animationName = buf.readString();
        boolean firstPerson = buf.readBoolean();
        if (player != null) {
            var animationContainer = ((ISoulForgeAnimatedPlayer) player).soulforge_getModAnimation();

            KeyframeAnimation anim = PlayerAnimationRegistry.getAnimation(new Identifier(SoulForge.MOD_ID, animationName));
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
