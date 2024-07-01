package com.pulsar.soulforge.client.entity;

import com.mojang.datafixers.util.Pair;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.render.CubeRenderer;
import com.pulsar.soulforge.entity.ImmobilizationEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.awt.*;

public class ImmobilizationEntityRenderer extends EntityRenderer<ImmobilizationEntity> {
    private static final Identifier TEXTURE = Identifier.of(SoulForge.MOD_ID, "textures/dome.png");

    public ImmobilizationEntityRenderer(Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ImmobilizationEntity immobilizationEntity) {
        return TEXTURE;
    }

    public void render(ImmobilizationEntity immobilizationEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.shadowRadius = 0f;
        CubeRenderer.renderCube(matrixStack.peek().getPositionMatrix(), vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE)),
                new Vec3d(-immobilizationEntity.getSizeX()/2f, 0f, -immobilizationEntity.getSizeX()/2f).toVector3f(),
                new Vec3d(immobilizationEntity.getSizeX()/2f, immobilizationEntity.getSizeY(), immobilizationEntity.getSizeX()/2f).toVector3f(),
                new Color(1f, 1f, 1f, 0.5f));
    }
}
