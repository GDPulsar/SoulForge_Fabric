package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
import com.pulsar.soulforge.client.render.CylinderRenderer;
import com.pulsar.soulforge.entity.BlastEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class BlastRenderer extends EntityRenderer<BlastEntity> {
    public static Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/white.png");

    public BlastRenderer(Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(BlastEntity blastEntity) {
        return TEXTURE;
    }

    protected int getBlockLight(BlastEntity blastEntity, BlockPos blockPos) {
        return 1;
    }

    public void render(BlastEntity blastEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(SoulForgeClient.getBeamRenderLayer(TEXTURE));
        float size;
        if (blastEntity.timer <= 3) {
            size = MathHelper.lerp(blastEntity.timer/3f, 0f, blastEntity.getRadius());
        } else if (blastEntity.getDuration()-blastEntity.timer > 10) {
            size = blastEntity.getRadius();
        } else {
            size = MathHelper.lerp((blastEntity.getDuration()-blastEntity.timer)/10f, 0f, blastEntity.getRadius());
        }
        CylinderRenderer.renderCylinder(matrix, vertexConsumer,  blastEntity.getStart().toVector3f(), blastEntity.getEnd().toVector3f(), size, blastEntity.getColor(), 0, 255, true);
    }
}
