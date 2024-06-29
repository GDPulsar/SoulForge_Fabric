package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.SpokenTextRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at=@At("TAIL"))
    private void onPlayerEntityRender(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        /*if (abstractClientPlayerEntity != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(abstractClientPlayerEntity);
            if (playerSoul != null) {
                String toRender = playerSoul.getSpokenText();
                if (Objects.equals(toRender, "")) {
                    playerSoul.setSpokenText("TESTING!!!", 4, 60);
                    return;
                }
                TextRenderer textRenderer = MinecraftClientMixin.getInstance().textRenderer;
                if (Objects.equals(toRender, "")) return;
                List<OrderedText> wrapped = textRenderer.wrapLines(StringVisitable.plain(toRender), 100);
                int width = textRenderer.getWidth(toRender);
                int height = textRenderer.getWrappedLinesHeight(toRender, 100);
                Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
                positionMatrix.translate(0f, 2.4f, 0f);
                positionMatrix.scale(-0.025f);
                int j = 0;
                for (OrderedText draw : wrapped) {
                    if (j == wrapped.size() - 1) {
                        textRenderer.draw(draw, -width / 2f, -(height + 5) * (wrapped.size() - j), 0xFFFFFF, true, positionMatrix, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0x888888, 255);
                    } else {
                        textRenderer.draw(draw, -50, -(height + 5) * (wrapped.size() - j), 0xFFFFFF, true, positionMatrix, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0x888888, 255);
                    }
                    j++;
                }
            }
        }*/
    }
}
