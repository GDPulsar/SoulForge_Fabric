package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
import com.pulsar.soulforge.entity.FrozenEnergyProjectile;
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
public class FrozenEnergyRenderer
        extends EntityRenderer<FrozenEnergyProjectile> {
    public static final Identifier TEXTURE = Identifier.of(SoulForge.MOD_ID, "textures/item/patience.png");
    private final FrozenEnergyModel model;

    public FrozenEnergyRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new FrozenEnergyModel(context.getPart(SoulForgeClient.MODEL_FROZEN_ENERGY_LAYER));
    }

    @Override
    public void render(FrozenEnergyProjectile frozenEnergy, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(g, frozenEnergy.prevYaw, frozenEnergy.getYaw()) - 90.0f));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(g, frozenEnergy.prevPitch, frozenEnergy.getPitch()) + 90.0f));
        VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(this.getTexture(frozenEnergy)), false, false);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 0xFFFFFF);
        super.render(frozenEnergy, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(FrozenEnergyProjectile frozenEnergy) {
        return TEXTURE;
    }
}
