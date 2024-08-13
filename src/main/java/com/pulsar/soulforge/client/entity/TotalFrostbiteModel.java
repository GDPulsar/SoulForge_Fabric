package com.pulsar.soulforge.client.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class TotalFrostbiteModel extends Model {
    private final ModelPart frostbite;

    public TotalFrostbiteModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.frostbite = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("frostbite", ModelPartBuilder.create().uv(1, 1).cuboid(-12f, 0, -12f, 24, 48, 24),
                ModelTransform.of(0f, 0f, 0f, 0f, 0f, 0f));
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        frostbite.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}