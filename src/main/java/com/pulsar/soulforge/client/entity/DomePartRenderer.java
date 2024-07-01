package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.DomeEntity;
import com.pulsar.soulforge.entity.DomePart;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class DomePartRenderer extends EntityRenderer<DomePart> {
    public DomePartRenderer(Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(DomePart domeEntity) {
        return null;
    }

    protected int getBlockLight(DomePart domeEntity, BlockPos blockPos) {
        return 1;
    }

    public void render(DomePart domeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {}
}
