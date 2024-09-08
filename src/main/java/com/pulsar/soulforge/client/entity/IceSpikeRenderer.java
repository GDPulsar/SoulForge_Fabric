package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.IceSpikeProjectile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value=EnvType.CLIENT)
public class IceSpikeRenderer
        extends EntityRenderer<IceSpikeProjectile> {
    public static final Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/item/patience.png");
    private final IceSpikeModel model;

    public IceSpikeRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new IceSpikeModel(context.getPart(SoulForgeClient.MODEL_ICE_SPIKE_LAYER));
    }

    @Override
    public void render(IceSpikeProjectile iceSpike, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.translate(0f, 5.5f, 0f);
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(iceSpike.getYaw() + 180f));
        float moveDist = -MathHelper.clampedLerp(-1.5f, 1.5f, (iceSpike.age + g) / 5f);
        matrixStack.translate(0f, MathHelper.sin(22.5f * MathHelper.RADIANS_PER_DEGREE) * moveDist, MathHelper.cos(22.5f * MathHelper.RADIANS_PER_DEGREE) * moveDist);
        if (iceSpike.getOwner() instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.getLV() >= 5) {
                matrixStack.scale(playerSoul.getLV() / 5f, playerSoul.getLV() / 5f, playerSoul.getLV() / 5f);
            }
        }
        VertexConsumer vertexConsumer = ItemRenderer.getItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(this.getTexture(iceSpike)), true, false);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, MathHelper.clampedLerp(1f, 0f, ((iceSpike.age + g) - 40) / 20f));
        super.render(iceSpike, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(IceSpikeProjectile iceSpike) {
        return TEXTURE;
    }
}
