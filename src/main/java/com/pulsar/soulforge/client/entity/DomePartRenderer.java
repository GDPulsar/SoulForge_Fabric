package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.block.SoulForgeBlocks;
import com.pulsar.soulforge.entity.DomePart;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class DomePartRenderer extends EntityRenderer<DomePart> {
    public static Identifier KINDNESS = new Identifier(SoulForge.MOD_ID, "textures/item/kindness.png");
    public static Identifier DETERMINATION = new Identifier(SoulForge.MOD_ID, "textures/item/determination.png");

    public DomePartRenderer(Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(DomePart domeEntity) {
        return null;
    }

    protected int getBlockLight(DomePart domeEntity, BlockPos blockPos) {
        return 1;
    }

    public void render(DomePart domeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (!domeEntity.getBlockStateAtPos().isSolidBlock(domeEntity.getWorld(), domeEntity.getBlockPos())) {
            //matrixStack.translate(-0.5f, 0f, -0.5f);
            BlockState state = domeEntity.isDetermination() ? SoulForgeBlocks.DETERMINATION_DOME_BLOCK.getDefaultState() : SoulForgeBlocks.DOME_BLOCK.getDefaultState();
            MinecraftClient.getInstance().getBlockRenderManager().renderBlock(state, domeEntity.getBlockPos(), domeEntity.getWorld(), matrixStack,
                    vertexConsumerProvider.getBuffer(RenderLayer.getTranslucent()), true, Random.create());
        }
    }
}
