package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.render.CylinderRenderer;
import com.pulsar.soulforge.entity.JusticeHarpoonProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Environment(value=EnvType.CLIENT)
public class JusticeHarpoonRenderer extends GeoEntityRenderer<JusticeHarpoonProjectile> {
    public static final Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/item/justice.png");

    public JusticeHarpoonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new JusticeHarpoonModel());
    }

    @Override
    public void render(JusticeHarpoonProjectile projectile, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        if (projectile.getOwner() != null) {
            VertexConsumer consumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
            Vector3f offset = projectile.getOwner().getPos().add(0f, 1f, 0f).toVector3f().sub(projectile.getPos().toVector3f());
            CylinderRenderer.renderCylinder(matrixStack.peek().getPositionMatrix(), consumer, new Vector3f(), offset, 0.1f, SoulForge.GDPULSAR, 0, 255, false);
        }
        matrixStack.pop();
        super.render(projectile, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(JusticeHarpoonProjectile projectile) {
        return TEXTURE;
    }
}
