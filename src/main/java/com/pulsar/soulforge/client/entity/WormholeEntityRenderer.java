package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.WormholeEntity;
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
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;

import java.awt.*;
import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class WormholeEntityRenderer extends EntityRenderer<WormholeEntity> {
    private static final ArrayList<Identifier> RIFT_CRACKS = new ArrayList<>();
    private static final ArrayList<Identifier> RIFT_CRACKS_BLOOMS = new ArrayList<>();
    public static final ArrayList<RenderLayer> RIFT_CRACKS_RENDER_TYPES = new ArrayList<>();
    public static final ArrayList<RenderLayer> RIFT_CRACKS_BLOOMS_RENDER_TYPES = new ArrayList<>();

    public WormholeEntityRenderer(Context context) {
        super(context);
    }

    public static void initialiseCrackRenderTypes() {
        for(int i = 1; i <= 5; ++i) {
            Identifier crack = new Identifier(SoulForge.MOD_ID, "textures/vfx/rift_crack_" + i + ".png");
            Identifier bloom = new Identifier(SoulForge.MOD_ID, "textures/vfx/rift_crack_bloom_" + i + ".png");
            RIFT_CRACKS.add(crack);
            RIFT_CRACKS_BLOOMS.add(bloom);
            RIFT_CRACKS_RENDER_TYPES.add(LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE_TRIANGLE.apply(RenderTypeToken.createToken(crack)));
            RIFT_CRACKS_BLOOMS_RENDER_TYPES.add(LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE_TRIANGLE.apply(RenderTypeToken.createToken(bloom)));
        }
    }

    @Override
    public Identifier getTexture(WormholeEntity wormhole) {
        return RIFT_CRACKS.get(0);
    }

    protected int getBlockLight(WormholeEntity wormhole, BlockPos blockPos) {
        return 1;
    }

    public void render(WormholeEntity rift, float yaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setVertexSupplier((consumer, stack, vfxBuilder, x, y, z, u, v) -> {
            if (stack == null) {
                consumer.vertex(x, y, z).color(1f, 1f, 1f,1f).texture(u, v).light(15).next();
            } else {
                consumer.vertex(stack, x, y, z).color(1f, 1f, 1f, 1f).texture(u, v).light(15).next();
            }

        }).setFormat(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
        float x = (float) MathHelper.lerp(partialTicks, rift.lastRenderX, rift.getX());
        float y = (float) MathHelper.lerp(partialTicks, rift.lastRenderY, rift.getY());
        float z = (float) MathHelper.lerp(partialTicks, rift.lastRenderZ, rift.getZ());
        matrixStack.translate(-x, -y, -z);

        for (Vec3d crack : rift.cracks) {
            Vec3d toPos = crack.add(rift.getPos());
            float spreadTime = 3.0F;
            if ((rift.age + partialTicks) <= spreadTime) {
                toPos = rift.getPos().add(crack.multiply(((rift.age + partialTicks) / spreadTime)));
            }

            builder.setRenderType(RIFT_CRACKS_RENDER_TYPES.get(rift.cracks.indexOf(crack) % 5));
            builder.setColor(new Color(0xFFFFFF)).setAlpha(1.0F).renderBeam(matrixStack.peek().getPositionMatrix(), rift.getPos(), toPos, 2.0F * rift.getSize());
            builder.setRenderType(RIFT_CRACKS_BLOOMS_RENDER_TYPES.get(rift.cracks.indexOf(crack) % 5));
            builder.setColor(new Color(0x00FF90)).setAlpha(1.0F).renderBeam(matrixStack.peek().getPositionMatrix(), rift.getPos(), toPos, 2.0F * rift.getSize());

            builder.setVertexConsumer(RenderHandler.DELAYED_RENDER.getTarget().getBuffer(RIFT_CRACKS_BLOOMS_RENDER_TYPES.get(rift.cracks.indexOf(crack) % 5)));
            for (int i = (int)(2 * rift.getSize()); i <= (int)(5 * rift.getSize()); ++i) {
                builder.setColor(new Color(0.0F, 1.0F, (float) i / (5.0F * rift.getSize()))).setAlpha(MathHelper.clamp((MathHelper.sin((rift.age + partialTicks + (float) i / rift.getSize() * 20.0F) / 10.0F) + (float) i / rift.getSize() / 2.0F) / (2.0F * ((float) i / rift.getSize() / 2.0F)), 0.0F, 1.0F) / rift.getSize()).renderBeam(matrixStack.peek().getPositionMatrix(), rift.getPos(), toPos, (float) i);
            }
        }
    }
}
