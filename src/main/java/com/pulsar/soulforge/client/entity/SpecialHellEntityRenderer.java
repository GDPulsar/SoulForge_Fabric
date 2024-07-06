package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.render.CylinderRenderer;
import com.pulsar.soulforge.entity.SpecialHellEntity;
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
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class SpecialHellEntityRenderer extends EntityRenderer<SpecialHellEntity> {
    public static Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/white.png");

    public SpecialHellEntityRenderer(Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(SpecialHellEntity specialHellEntity) {
        return TEXTURE;
    }

    protected int getBlockLight(SpecialHellEntity specialHellEntity, BlockPos blockPos) {
        return 1;
    }

    public void render(SpecialHellEntity specialHellEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(TEXTURE));
        float top = 100f;
        if (specialHellEntity.timer >= 100) top = MathHelper.lerp((specialHellEntity.timer-100)/40f, 100f, 0f);
        if (specialHellEntity.timer < 60) top = 0.1f;
        CylinderRenderer.renderCylinder(matrix, vertexConsumer,  new Vector3f(0f, -10f, 0f), new Vector3f(0f, top, 0f), 10f, new Color(1f, 0.6f, 0.6f, 1f), 0, 0, true);
    }
}
