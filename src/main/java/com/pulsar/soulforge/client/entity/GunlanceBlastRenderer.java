package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.render.CylinderRenderer;
import com.pulsar.soulforge.entity.GunlanceBlastEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class GunlanceBlastRenderer extends EntityRenderer<GunlanceBlastEntity> {
    public static Identifier TEXTURE = Identifier.of(SoulForge.MOD_ID, "textures/white.png");

    public GunlanceBlastRenderer(Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(GunlanceBlastEntity blastEntity) {
        return TEXTURE;
    }

    protected int getBlockLight(GunlanceBlastEntity blastEntity, BlockPos blockPos) {
        return 1;
    }

    public void render(GunlanceBlastEntity blastEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(TEXTURE));
        if (blastEntity.timer >= 10) {
            float size;
            if (blastEntity.timer <= 13) {
                size = MathHelper.lerp((blastEntity.timer - 10) / 3f, 0f, blastEntity.getRadius());
            } else {
                size = blastEntity.getRadius();
            }
            CylinderRenderer.renderCylinder(matrix, vertexConsumer, blastEntity.getStart().toVector3f(), blastEntity.getEnd().toVector3f(), size, blastEntity.getColor(), 0, 255, true);
        } else if (blastEntity.owner != null) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(Identifier.of(SoulForge.MOD_ID, "textures/placeholder.png"));
            Vec3d center = blastEntity.getPos();
            Vec3d right = blastEntity.owner.getRotationVector().rotateY(MathHelper.PI/2f).normalize().multiply(0.25f);
            Vec3d up = blastEntity.owner.getRotationVector().rotateX(MathHelper.PI/2f).normalize().multiply(0.25f);
            renderQuad(matrix, vertexConsumer,
                    center.add(right).add(up).toVector3f(),
                    center.add(right).subtract(up).toVector3f(),
                    center.subtract(right).subtract(up).toVector3f(),
                    center.subtract(right).add(up).toVector3f());
        }
    }

    private void renderQuad(
            Matrix4f model,
            VertexConsumer vertices,
            Vector3f p1,
            Vector3f p2,
            Vector3f p3,
            Vector3f p4
    ) {
        vertices.vertex(model, p1.x, p1.y, p1.z).texture(1, 1).overlay(0).light(255).normal(0, 1, 0);
        vertices.vertex(model, p2.x, p2.y, p2.z).texture(1, 0).overlay(0).light(255).normal(0, 1, 0);
        vertices.vertex(model, p3.x, p3.y, p3.z).texture(0, 0).overlay(0).light(255).normal(0, 1, 0);
        vertices.vertex(model, p4.x, p4.y, p4.z).texture(0, 1).overlay(0).light(255).normal(0, 1, 0);
    }
}
