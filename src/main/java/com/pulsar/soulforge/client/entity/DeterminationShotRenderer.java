package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.render.SphereRenderer;
import com.pulsar.soulforge.entity.DeterminationShotProjectile;
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

import java.awt.*;

@Environment(EnvType.CLIENT)
public class DeterminationShotRenderer extends EntityRenderer<DeterminationShotProjectile> {
    private static final Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/white.png");

    public DeterminationShotRenderer(Context context) {
        super(context);
    }

    public void render(DeterminationShotProjectile energyBallEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(TEXTURE));
        SphereRenderer.renderSphere(matrix, vertexConsumer, 0.2f, Color.RED);
    }

    public Identifier getTexture(DeterminationShotProjectile energyBallEntity) {
        return TEXTURE;
    }
}
