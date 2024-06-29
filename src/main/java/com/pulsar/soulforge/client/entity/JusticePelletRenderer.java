package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.render.SphereRenderer;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
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
public class JusticePelletRenderer extends EntityRenderer<JusticePelletProjectile> {
    private static final Identifier JUSTICE_TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/item/justice.png");
    private static final Identifier KINDNESS_TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/item/kindness.png");

    public JusticePelletRenderer(Context context) {
        super(context);
    }

    public void render(JusticePelletProjectile pelletEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Identifier texture = pelletEntity.getDamage() > 0 ? JUSTICE_TEXTURE : KINDNESS_TEXTURE;
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(texture));
        SphereRenderer.renderSphere(matrix, vertexConsumer, 0.125f);
    }

    public Identifier getTexture(JusticePelletProjectile pelletEntity) {
        return pelletEntity.getDamage() > 0 ? JUSTICE_TEXTURE : KINDNESS_TEXTURE;
    }
}
