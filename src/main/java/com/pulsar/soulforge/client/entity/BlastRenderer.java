package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.BlastEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import team.lodestar.lodestone.helpers.RenderHelper;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.registry.client.LodestoneShaderRegistry;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.StateShards;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeProvider;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;

import java.awt.*;

import static net.minecraft.client.render.RenderPhase.ENABLE_CULLING;
import static net.minecraft.client.render.RenderPhase.ENABLE_LIGHTMAP;
import static net.minecraft.client.render.VertexFormat.DrawMode.TRIANGLES;
import static net.minecraft.client.render.VertexFormats.POSITION_COLOR_TEXTURE_LIGHT;

@Environment(EnvType.CLIENT)
public class BlastRenderer extends EntityRenderer<BlastEntity> {
    public static final RenderTypeProvider TRANSPARENT_TEXTURE_TRIANGLE_TRUE = new RenderTypeProvider((token) ->
            LodestoneRenderTypeRegistry.createGenericRenderType("transparent_texture_triangles", POSITION_COLOR_TEXTURE_LIGHT, TRIANGLES, LodestoneRenderTypeRegistry.builder()
                    .setShaderState(LodestoneShaderRegistry.TRIANGLE_TEXTURE)
                    .setTransparencyState(StateShards.NORMAL_TRANSPARENCY)
                    .setLightmapState(ENABLE_LIGHTMAP)
                    .setCullState(ENABLE_CULLING)
                    .setTextureState(token.get())));
    public static final Identifier MAIN_TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/white.png");
    public static final Identifier ENERGY_TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/entity/energy_beam.png");
    public static final LodestoneRenderType MAIN_RENDER_TYPE_TRIANGLE = TRANSPARENT_TEXTURE_TRIANGLE_TRUE.apply(RenderTypeToken.createToken(MAIN_TEXTURE));
    public static final LodestoneRenderType ENERGY_RENDER_TYPE_TRIANGLE = TRANSPARENT_TEXTURE_TRIANGLE_TRUE.apply(RenderTypeToken.createToken(ENERGY_TEXTURE));
    public static final LodestoneRenderType MAIN_RENDER_TYPE_QUADS = LodestoneRenderTypeRegistry.TRANSPARENT_TEXTURE.apply(RenderTypeToken.createToken(MAIN_TEXTURE));
    public static final LodestoneRenderType ENERGY_RENDER_TYPE_QUADS = LodestoneRenderTypeRegistry.TRANSPARENT_TEXTURE.apply(RenderTypeToken.createToken(ENERGY_TEXTURE));

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
        matrixStack.push();
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

        builder.setRenderType(MAIN_RENDER_TYPE_QUADS);
        builder.setColor(blast.getColor()).setAlpha(1f).renderBeam(matrixStack.peek().getPositionMatrix(), blast.getPos(), toPos, 2f * blast.getRadius());
        Vec3d cameraPosition = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().camera.getPos();
        Vec3d normal = blast.getPos().subtract(cameraPosition).crossProduct(toPos.subtract(blast.getPos())).normalize().multiply(0.01f);
        matrixStack.translate(normal.x, normal.y, normal.z);
        builder.setRenderType(ENERGY_RENDER_TYPE_QUADS);
        builder.setColor(blast.getColor()).setAlpha(0.3f).renderBeam(matrixStack.peek().getPositionMatrix(), blast.getPos(), toPos, 2f * blast.getRadius());
        matrixStack.pop();
        /*matrixStack.translate(-normal.x, -normal.y, -normal.z);
        matrixStack.translate(x, y, z);
        Color color = new Color(blast.getColor().getRed(), blast.getColor().getGreen(), blast.getColor().getBlue(), 255);
        renderSphereGood(vertexConsumerProvider.getBuffer(MAIN_RENDER_TYPE_TRIANGLE), matrixStack, blast.getRadius() - 1f, 25, 25, color, light);
        color = new Color(blast.getColor().getRed(), blast.getColor().getGreen(), blast.getColor().getBlue(), (int)(255 * 0.3f));
        renderSphereGood(vertexConsumerProvider.getBuffer(ENERGY_RENDER_TYPE_TRIANGLE), matrixStack, blast.getRadius() - 0.99f, 25, 25, color, light);*/
    }

    public void renderSphereGood(VertexConsumer consumer, MatrixStack stack, float radius, int longs, int lats, Color color, int light) {
        Matrix4f last = stack.peek().getPositionMatrix();
        float startU = 0.0F;
        float startV = 0.0F;
        float endU = 6.2831855F;
        float endV = 3.1415927F;
        float stepU = (endU - startU) / (float)longs;
        float stepV = (endV - startV) / (float)lats;

        int r = color.getRed(), g = color.getGreen(), b = color.getBlue(), a = color.getAlpha();
        for(int i = 0; i < longs; ++i) {
            for(int j = 0; j < lats; ++j) {
                float u = (float)i * stepU + startU;
                float v = (float)j * stepV + startV;
                float un = i + 1 == longs ? endU : (float)(i + 1) * stepU + startU;
                float vn = j + 1 == lats ? endV : (float)(j + 1) * stepV + startV;
                Vector3f p0 = RenderHelper.parametricSphere(u, v, radius);
                Vector3f p1 = RenderHelper.parametricSphere(u, vn, radius);
                Vector3f p2 = RenderHelper.parametricSphere(un, v, radius);
                Vector3f p3 = RenderHelper.parametricSphere(un, vn, radius);
                float textureU = u / endU;
                float textureV = v / endV;
                float textureUN = un / endU;
                float textureVN = vn / endV;
                RenderHelper.vertexPosColorUVLight(consumer, last, p0.x(), p0.y(), p0.z(), r, g, b, a, textureU, textureV, light);
                RenderHelper.vertexPosColorUVLight(consumer, last, p2.x(), p2.y(), p2.z(), r, g, b, a, textureUN, textureV, light);
                RenderHelper.vertexPosColorUVLight(consumer, last, p1.x(), p1.y(), p1.z(), r, g, b, a, textureU, textureVN, light);
                RenderHelper.vertexPosColorUVLight(consumer, last, p3.x(), p3.y(), p3.z(), r, g, b, a, textureUN, textureVN, light);
                RenderHelper.vertexPosColorUVLight(consumer, last, p1.x(), p1.y(), p1.z(), r, g, b, a, textureU, textureVN, light);
                RenderHelper.vertexPosColorUVLight(consumer, last, p2.x(), p2.y(), p2.z(), r, g, b, a, textureUN, textureV, light);
            }
        }
    }
}
