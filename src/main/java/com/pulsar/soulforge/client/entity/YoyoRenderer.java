package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.render.CylinderRenderer;
import com.pulsar.soulforge.entity.JusticeHarpoonProjectile;
import com.pulsar.soulforge.entity.YoyoProjectile;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.awt.*;

public class YoyoRenderer extends GeoEntityRenderer<YoyoProjectile> {
    public static final Identifier TEXTURE = Identifier.of(SoulForge.MOD_ID, "textures/item/integrity.png");

    public YoyoRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new YoyoModel());
    }

    @Override
    public void render(YoyoProjectile projectile, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        if (projectile.getOwner() != null) {
            if (projectile.distanceTo(projectile.getOwner()) <= 0.3f) return;
            VertexConsumer consumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
            Vector3f offset = projectile.getOwner().getPos().add(0f, 1f, 0f).toVector3f().sub(projectile.getPos().toVector3f());
            CylinderRenderer.renderCylinder(matrixStack.peek().getPositionMatrix(), consumer, new Vector3f(), offset, 0.02f, Color.WHITE, 0, 255, false);
        }
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, projectile.prevYaw, projectile.getYaw()) - 90.0f));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(g, projectile.prevPitch, projectile.getPitch()) + 90.0f));
        matrixStack.pop();
        super.render(projectile, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(YoyoProjectile projectile) {
        return TEXTURE;
    }
}
