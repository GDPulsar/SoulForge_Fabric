package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.features.AuraShineFeatureRenderer;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
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
        if (abstractClientPlayerEntity != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(abstractClientPlayerEntity);
            String toRender = playerSoul.getSpokenText();
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            if (Objects.equals(toRender, "")) return;
            List<OrderedText> wrapped = textRenderer.wrapLines(StringVisitable.plain(toRender), 160);
            int height = textRenderer.getWrappedLinesHeight(toRender, 160);
            Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
            PlayerEntityRenderer renderer = (PlayerEntityRenderer)(Object)this;
            Camera camera = renderer.dispatcher.camera;
            Quaternionf rotation = new Quaternionf().rotationYXZ(-0.017453292F * camera.getYaw(), -0.017453292F * camera.getPitch(), 0.0F);
            positionMatrix.rotate(rotation);
            positionMatrix.translate(0f, 2.4f, 0f);
            positionMatrix.scale(-0.025f);
            int j = 0;
            for (OrderedText draw : wrapped) {
                textRenderer.draw(draw, -textRenderer.getWidth(draw) / 2f, -height * (wrapped.size() - j), 0xFFFFFF, true, positionMatrix, vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0x888888, 255);
                j++;
            }
        }
    }

    @ModifyReturnValue(method = "getTexture(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)Lnet/minecraft/util/Identifier;", at = @At("RETURN"))
    public Identifier getTexture(Identifier original, @Local AbstractClientPlayerEntity abstractClientPlayerEntity) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(abstractClientPlayerEntity);
        if (playerSoul.getDisguise() != null) {
            PlayerListEntry entry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(playerSoul.getDisguise().getUuid());
            if (entry != null) return entry.getSkinTexture();
        }
        return abstractClientPlayerEntity.getSkinTexture();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addFeatureRenderers(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        PlayerEntityRenderer renderer = (PlayerEntityRenderer)(Object)this;
        renderer.features.add(new AuraShineFeatureRenderer(ctx, slim, renderer));
    }
}
