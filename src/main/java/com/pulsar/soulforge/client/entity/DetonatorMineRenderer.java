package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.render.CubeRenderer;
import com.pulsar.soulforge.client.render.CylinderRenderer;
import com.pulsar.soulforge.entity.DeterminationPlatformEntity;
import com.pulsar.soulforge.entity.DetonatorMine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class DetonatorMineRenderer extends EntityRenderer<DetonatorMine> {
    public static Identifier TEXTURE = Identifier.of(SoulForge.MOD_ID, "textures/item/bravery.png");

    public DetonatorMineRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(DetonatorMine mine) {
        return TEXTURE;
    }

    protected int getBlockLight(DetonatorMine mine, BlockPos blockPos) {
        return 1;
    }

    public void render(DetonatorMine mine, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(TEXTURE));
        CubeRenderer.renderCube(matrix, vertexConsumer, new Vector3f(-0.25f, -0.25f, -0.25f), new Vector3f(0.25f, 0.25f, 0.25f), Color.WHITE);
    }
}
