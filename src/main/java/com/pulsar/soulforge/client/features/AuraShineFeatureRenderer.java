package com.pulsar.soulforge.client.features;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.trait.Traits;
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
        boolean bravery = playerSoul.hasCast("Bravery Boost") || playerSoul.hasCast("Fearless Instincts") || playerSoul.hasCast("Perfected Aura Technique") || playerSoul.getTraits().contains(Traits.spite);
        boolean justice = playerSoul.hasCast("Accelerated Pellet Aura") || playerSoul.getTraits().contains(Traits.spite);
        boolean kindness = playerSoul.getTraits().contains(Traits.spite);
        boolean patience = playerSoul.getTraits().contains(Traits.spite);
        boolean integrity = playerSoul.hasCast("Repulsion Field") || playerSoul.hasCast("Fearless Instincts") || playerSoul.hasCast("Accelerated Pellet Aura") || playerSoul.getTraits().contains(Traits.spite);
        boolean perseverance = playerSoul.hasCast("Perseverance Aura") || playerSoul.hasCast("Perfected Aura Technique") || playerSoul.getTraits().contains(Traits.spite);
        boolean determination = playerSoul.hasCast("Determination Aura") || playerSoul.getTraits().contains(Traits.spite);
        if (bravery) {
            float f = (float)entity.age + tickDelta;
            this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.getContextModel().copyStateTo(this.model);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(SKIN, f * 0.006f % 1.0F, f * 0.003F % 1.0F));
            this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1f, 0.5f, 0f, 1f);
        }
        if (justice) {
            float f = (float)entity.age + tickDelta;
            this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.getContextModel().copyStateTo(this.model);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(SKIN, f * 0.006f % 1.0F, f * 0.003F % 1.0F));
            this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1f, 1f, 0f, 1f);
        }
        if (kindness) {
            float f = (float)entity.age + tickDelta;
            this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.getContextModel().copyStateTo(this.model);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(SKIN, f * 0.006f % 1.0F, f * 0.003F % 1.0F));
            this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 0f, 1f, 0f, 1f);
        }
        if (patience) {
            float f = (float)entity.age + tickDelta;
            this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.getContextModel().copyStateTo(this.model);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(SKIN, f * 0.006f % 1.0F, f * 0.003F % 1.0F));
            this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 0f, 1f, 1f, 1f);
        }
        if (integrity) {
            float f = (float)entity.age + tickDelta;
            this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.getContextModel().copyStateTo(this.model);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(SKIN, f * 0.006f % 1.0F, f * 0.003F % 1.0F));
            this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 0f, 0f, 1f, 1f);
        }
        if (perseverance) {
            float f = (float)entity.age + tickDelta;
            this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.getContextModel().copyStateTo(this.model);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(SKIN, f * 0.006f % 1.0F, f * 0.003F % 1.0F));
            this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 0.5f, 0f, 1f, 1f);
        }
        if (determination) {
            float f = (float)entity.age + tickDelta;
            this.model.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.getContextModel().copyStateTo(this.model);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(SKIN, f * 0.006f % 1.0F, f * 0.003F % 1.0F));
            this.model.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1f, 0f, 0f, 1f);
        }
    }
}
