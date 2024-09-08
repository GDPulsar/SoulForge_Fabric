package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.BlastEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;

@Environment(EnvType.CLIENT)
public class BlastRenderer extends EntityRenderer<BlastEntity> {
    public static final Identifier MAIN_TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/white.png");
    public static final Identifier ENERGY_TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/entity/energy_beam.png");
    public static final RenderLayer MAIN_RENDER_TYPE = LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE_TRIANGLE.apply(RenderTypeToken.createToken(MAIN_TEXTURE));
    public static final RenderLayer ENERGY_RENDER_TYPE = LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE_TRIANGLE.apply(RenderTypeToken.createToken(ENERGY_TEXTURE));

    public BlastRenderer(Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(BlastEntity blastEntity) {
        return MAIN_TEXTURE;
    }

    protected int getBlockLight(BlastEntity blastEntity, BlockPos blockPos) {
        return 1;
    }

    public void render(BlastEntity blast, float f, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setVertexSupplier((consumer, stack, vfxBuilder, x, y, z, u, v) -> {
            if (stack == null) {
                consumer.vertex(x, y, z).color(1f, 1f, 1f,1f).texture(u, v).light(15).next();
            } else {
                consumer.vertex(stack, x, y, z).color(1f, 1f, 1f, 1f).texture(u, v).light(15).next();
            }

        }).setFormat(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
        float x = (float) MathHelper.lerp(partialTicks, blast.lastRenderX, blast.getX());
        float y = (float) MathHelper.lerp(partialTicks, blast.lastRenderY, blast.getY());
        float z = (float) MathHelper.lerp(partialTicks, blast.lastRenderZ, blast.getZ());
        matrixStack.translate(-x, -y, -z);

        Vec3d toPos = blast.getEnd().add(blast.getPos());

        builder.setRenderType(MAIN_RENDER_TYPE);
        builder.setColor(blast.getColor()).setAlpha(1f).renderBeam(matrixStack.peek().getPositionMatrix(), blast.getPos(), toPos, 2f * blast.getRadius());
        builder.setRenderType(ENERGY_RENDER_TYPE);
        builder.setColor(blast.getColor()).setAlpha(0.3f).renderBeam(matrixStack.peek().getPositionMatrix(), blast.getPos(), toPos, 2f * blast.getRadius() + 0.01f);
    }
}
