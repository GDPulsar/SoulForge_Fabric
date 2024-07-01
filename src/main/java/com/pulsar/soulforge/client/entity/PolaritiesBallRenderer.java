package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.render.SphereRenderer;
import com.pulsar.soulforge.entity.EnergyBallProjectile;
import com.pulsar.soulforge.entity.PolarityBall;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class PolaritiesBallRenderer extends EntityRenderer<PolarityBall> {
    private static final Identifier INVERSE_TEXTURE = Identifier.of(SoulForge.MOD_ID, "textures/item/bravery.png");
    private static final Identifier REVERSE_TEXTURE = Identifier.of(SoulForge.MOD_ID, "textures/item/patience.png");

    public PolaritiesBallRenderer(Context context) {
        super(context);
    }

    public void render(PolarityBall polarityBall, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(getTexture(polarityBall)));
        SphereRenderer.renderSphere(matrix, vertexConsumer, 0.25f);
    }

    public Identifier getTexture(PolarityBall polarityBall) {
        if (polarityBall.getInverse()) return INVERSE_TEXTURE;
        return REVERSE_TEXTURE;
    }
}
