package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForgeClient;
import com.pulsar.soulforge.entity.TotalFrostbiteEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class TotalFrostbiteEntityRenderer extends EntityRenderer<TotalFrostbiteEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/block/ice.png");
    private final TotalFrostbiteModel model;

    public TotalFrostbiteEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new TotalFrostbiteModel(context.getPart(SoulForgeClient.MODEL_TOTAL_FROSTBITE_LAYER));
    }

    @Override
    public void render(TotalFrostbiteEntity frostbite, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(this.getTexture(frostbite)), true, false);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        super.render(frostbite, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(TotalFrostbiteEntity frostbite) {
        return TEXTURE;
    }
}
