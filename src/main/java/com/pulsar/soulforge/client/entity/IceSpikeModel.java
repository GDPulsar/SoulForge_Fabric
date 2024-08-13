package com.pulsar.soulforge.client.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class IceSpikeModel extends Model {
    private final ModelPart spike;

    public IceSpikeModel(ModelPart root) {
        super(RenderLayer::getEntityTranslucent);
        this.spike = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("spike", ModelPartBuilder.create().uv(1, 1).cuboid(-1.0F, -13.1179F, -0.3142F, 2.0F, 18.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 1).cuboid(-2.5F, -9.1179F, -0.3142F, 4.0F, 18.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 1).cuboid(-3.5F, -6.6274F, -0.0963F, 6.0F, 12.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 1).cuboid(-4.5F, -4.1369F, 0.1216F, 8.0F, 10.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 1).cuboid(-3.5F, -4.4531F, -2.0887F, 6.0F, 12.0F, 2.0F, new Dilation(0.0F))
                .uv(-2, -1).cuboid(-3.5F, -3.4133F, -2.4996F, 6.0F, 10.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 1).cuboid(-1.5F, -11.0307F, -1.3104F, 2.0F, 20.0F, 2.0F, new Dilation(0.0F))
                .uv(-1, 0).cuboid(-2.0F, -6.1653F, -1.729F, 4.0F, 14.0F, 4.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-1.0F, -8.6634F, -1.7725F, 2.0F, 18.0F, 4.0F, new Dilation(0.0F))
                .uv(-1, 0).cuboid(-2.5F, -5.7108F, -1.1873F, 4.0F, 14.0F, 4.0F, new Dilation(0.0F))
                .uv(-2, -1).cuboid(-2.5F, -3.1331F, -1.9656F, 4.0F, 8.0F, 6.0F, new Dilation(0.0F))
                .uv(-1, 0).cuboid(-1.5F, -3.7543F, -0.6892F, 4.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 23.5F, 0.0F, 1.1781F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        spike.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}