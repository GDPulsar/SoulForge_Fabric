/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class FrozenEnergyModel
        extends Model {
    public static final Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/item/patience.png");
    private final ModelPart root;

    public FrozenEnergyModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("centerSpike", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5f, 2.5f, -0.5f, 1.0f, 3.0f, 1.0f), ModelTransform.NONE);
        modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -0.5f, -1.5f, 3.0f, 3.0f, 3.0f), ModelTransform.NONE);
        modelPartData.addChild("side1", ModelPartBuilder.create().uv(0, 0).cuboid(-2.5f, -0.5f, -1.5f, 1.0f, 1.0f, 3.0f), ModelTransform.NONE);
        modelPartData.addChild("side2", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -0.5f, -2.5f, 3.0f, 1.0f, 1.0f), ModelTransform.NONE);
        modelPartData.addChild("side3", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -0.5f, 1.5f, 3.0f, 1.0f, 1.0f), ModelTransform.NONE);
        modelPartData.addChild("side4", ModelPartBuilder.create().uv(0, 0).cuboid(1.5f, -0.5f, -1.5f, 1.0f, 1.0f, 3.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}

