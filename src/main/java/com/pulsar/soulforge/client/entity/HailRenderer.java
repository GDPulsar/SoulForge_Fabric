package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
import com.pulsar.soulforge.entity.FrozenEnergyProjectile;
import com.pulsar.soulforge.entity.HailProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class HailRenderer
        extends EntityRenderer<HailProjectile> {
    public static final Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/item/patience.png");
    private final FrozenEnergyModel model;

    public HailRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new FrozenEnergyModel(context.getPart(SoulForgeClient.MODEL_FROZEN_ENERGY_LAYER));
    }

    @Override
    public void render(HailProjectile hailProjectile, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, hailProjectile.prevYaw, hailProjectile.getYaw()) - 90.0f));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(g, hailProjectile.prevPitch, hailProjectile.getPitch()) + 90.0f));
        VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(this.getTexture(hailProjectile)), false, false);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
        super.render(hailProjectile, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(HailProjectile hailProjectile) {
        return TEXTURE;
    }
}
