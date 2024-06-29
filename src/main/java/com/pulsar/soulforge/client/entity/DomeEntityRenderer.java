package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.render.CubeRenderer;
import com.pulsar.soulforge.client.render.SphereRenderer;
import com.pulsar.soulforge.entity.DomeEntity;
import com.pulsar.soulforge.entity.SphereDomeEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class DomeEntityRenderer extends EntityRenderer<DomeEntity> {
    private static final Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/dome.png");

    public DomeEntityRenderer(Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(DomeEntity domeEntity) {
        return null;
    }

    protected int getBlockLight(DomeEntity domeEntity, BlockPos blockPos) {
        return 1;
    }

    public void render(DomeEntity domeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        /*Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentEmissive(TEXTURE));
        SphereRenderer.renderSphere(matrix, vertexConsumer,  sphereDomeEntity.size/2f);
        for (Entity entity : sphereDomeEntity.getEntityWorld().getOtherEntities(sphereDomeEntity, sphereDomeEntity.getBoundingBox())) {
            Vec3d direction = (entity.getPos().add(0f, entity.getHeight()/2f, 0f).subtract(sphereDomeEntity.getPos())).normalize();
            Vec3d surfacePos = direction.multiply(sphereDomeEntity.size/2f);
            CubeRenderer.renderCube(matrix, vertexConsumer,
                    surfacePos.toVector3f().sub(0.1f, 0.1f, 0.1f),
                    surfacePos.toVector3f().add(0.1f, 0.1f, 0.1f),
                    Color.RED);
        }*/
    }
}
