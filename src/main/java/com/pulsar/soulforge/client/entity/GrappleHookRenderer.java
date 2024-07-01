package com.pulsar.soulforge.client.entity;

import com.google.common.collect.Lists;
import com.pulsar.soulforge.client.render.CylinderRenderer;
import com.pulsar.soulforge.entity.GrappleHookProjectile;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.awt.*;
import java.util.List;

public class GrappleHookRenderer extends GeoEntityRenderer<GrappleHookProjectile> {
    public GrappleHookRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new GrappleHookModel());
    }

    @Override
    public void render(GrappleHookProjectile entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        VertexConsumer consumer = bufferSource.getBuffer(RenderLayer.getEntityCutout(this.getTexture(entity)));
        if (entity.getOwner() != null) {
            List<Vec3d> positions = List.of(entity.getOwner().getPos());
            if (entity.positions != null && !entity.positions.isEmpty()) positions.addAll(entity.positions);
            for (int i = 0; i < positions.size() - 1; i++) {
                CylinderRenderer.renderCylinder(consumer, positions.get(i).subtract(entity.getPos()).toVector3f(), positions.get(i + 1).subtract(entity.getPos()).toVector3f(), 0.1f, Color.GRAY, 0, 255);
            }
        }
    }
}
