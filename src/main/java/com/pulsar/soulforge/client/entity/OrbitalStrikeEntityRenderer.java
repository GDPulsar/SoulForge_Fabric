package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.render.CylinderRenderer;
import com.pulsar.soulforge.entity.OrbitalStrikeEntity;
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
public class OrbitalStrikeEntityRenderer extends EntityRenderer<OrbitalStrikeEntity> {
    public static Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/white.png");

    public OrbitalStrikeEntityRenderer(Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(OrbitalStrikeEntity orbitalStrikeEntity) {
        return TEXTURE;
    }

    protected int getBlockLight(OrbitalStrikeEntity orbitalStrikeEntity, BlockPos blockPos) {
        return 1;
    }

    public void render(OrbitalStrikeEntity orbitalStrikeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(TEXTURE));
        CylinderRenderer.renderCylinder(matrix, vertexConsumer,  new Vector3f(0f, 0f, 0f), new Vector3f(0f, 20f, 0f), 2f, new Color(1f, 1f, 1f, 1f), 0, 255, true);
    }
}
