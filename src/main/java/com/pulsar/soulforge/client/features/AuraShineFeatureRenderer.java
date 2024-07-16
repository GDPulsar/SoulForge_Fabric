package com.pulsar.soulforge.client.features;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

import java.awt.*;

public class AuraShineFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static final Identifier SKIN = new Identifier(SoulForge.MOD_ID, "textures/entity/player_aura.png");
    private final PlayerEntityModel<AbstractClientPlayerEntity> model;

    public AuraShineFeatureRenderer(EntityRendererFactory.Context renderContext, boolean slim, FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
        this.model = new PlayerEntityModel<>(renderContext.getPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim);
        context.getModel().copyStateTo(this.model);
        float horizScale = 0.2f;
        float vertScale = 0.05f;
        this.model.head.scale(new Vector3f(horizScale, vertScale, horizScale));
        this.model.body.scale(new Vector3f(horizScale, vertScale, horizScale));
        this.model.rightArm.scale(new Vector3f(horizScale, vertScale, horizScale));
        this.model.leftArm.scale(new Vector3f(horizScale, vertScale, horizScale));
        this.model.rightLeg.scale(new Vector3f(horizScale, vertScale, horizScale));
        this.model.leftLeg.scale(new Vector3f(horizScale, vertScale, horizScale));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(entity);
        boolean hasAura = false;
        boolean hasSecondAura = false;
        Color auraColor = Color.WHITE;
        Color secondAuraColor = Color.WHITE;
        if (playerSoul.hasCast("Bravery Boost")) {
            hasAura = true;
            auraColor = new Color(255, 128, 0);
        }
        if (playerSoul.hasCast("Repulsion Field")) {
            hasSecondAura = true;
            secondAuraColor = new Color(0, 0, 255);
        }
        if (playerSoul.hasCast("Perseverance Aura")) {
            hasAura = true;
            auraColor = new Color(128, 0, 255);
        }
        if (playerSoul.hasCast("Determination Aura")) {
            hasAura = true;
            auraColor = new Color(255, 0, 0);
        }
        if (playerSoul.hasCast("Fearless Instincts")) {
            hasAura = true;
            hasSecondAura = true;
            auraColor = new Color(255, 128, 0);
            secondAuraColor = new Color(0, 0, 255);
        }
        if (playerSoul.hasCast("Perfected Aura Technique")) {
            hasAura = true;
            hasSecondAura = true;
            auraColor = new Color(255, 128, 0);
            secondAuraColor = new Color(128, 0, 255);
        }
        if (hasAura) {
            float f = (float)entity.age + tickDelta;
            this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.getContextModel().copyStateTo(this.model);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(SKIN, f * 0.006f % 1.0F, f * 0.003F % 1.0F));
            this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, auraColor.getRed() / 255f, auraColor.getGreen() / 255f, auraColor.getBlue() / 255f, 1f);
        }
        if (hasSecondAura) {
            float f = (float)entity.age + tickDelta;
            this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.getContextModel().copyStateTo(this.model);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(SKIN, f * -0.006f % 1.0F, f * -0.003F % 1.0F));
            this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, secondAuraColor.getRed() / 255f, secondAuraColor.getGreen() / 255f, secondAuraColor.getBlue() / 255f, 1f);
        }
    }
}
