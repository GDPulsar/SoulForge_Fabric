package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.DomeEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DomeEntityRenderer extends EntityRenderer<DomeEntity> {
    private static final Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/white.png");

    public DomeEntityRenderer(Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(DomeEntity domeEntity) {
        return TEXTURE;
    }

    public void render(DomeEntity domeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        /*Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentEmissive(TEXTURE));
        SphereRenderer.renderSphere(matrix, vertexConsumer,  domeEntity.getSize()/2f, new Color(0, 255, 0, 64));*/
    }
}
