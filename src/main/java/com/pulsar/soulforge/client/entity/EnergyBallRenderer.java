package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.render.SphereRenderer;
import com.pulsar.soulforge.entity.EnergyBallProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class EnergyBallRenderer extends EntityRenderer<EnergyBallProjectile> {
    private static final Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/item/bravery.png");

    public EnergyBallRenderer(Context context) {
        super(context);
    }

    public void render(EnergyBallProjectile energyBallEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(TEXTURE));
        SphereRenderer.renderSphere(matrix, vertexConsumer, 0.25f);
    }

    public Identifier getTexture(EnergyBallProjectile energyBallEntity) {
        return TEXTURE;
    }
}
