package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.render.CylinderRenderer;
import com.pulsar.soulforge.entity.DarkFountainEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class DarkFountainEntityRenderer extends EntityRenderer<DarkFountainEntity> {
    public static Identifier TEXTURE = Identifier.of(SoulForge.MOD_ID, "textures/white.png");

    public DarkFountainEntityRenderer(Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(DarkFountainEntity darkFountainEntity) {
        return TEXTURE;
    }

    protected int getBlockLight(DarkFountainEntity darkFountainEntity, BlockPos blockPos) {
        return 1;
    }

    public void render(DarkFountainEntity darkFountainEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(TEXTURE));
        CylinderRenderer.renderCylinder(matrix, vertexConsumer,  new Vector3f(0f, 0f, 0f), new Vector3f(0f, 5f, 0f), 2f, 4f, new Color(0f, 0f, 0f, 1f), 0, 0);
        CylinderRenderer.renderCylinder(matrix, vertexConsumer,  new Vector3f(0f, 5f, 0f), new Vector3f(0f, 40f, 0f), 4f, new Color(0f, 0f, 0f, 1f), 0, 0, true);
    }
}
