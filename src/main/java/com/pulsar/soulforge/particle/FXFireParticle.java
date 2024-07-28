package com.pulsar.soulforge.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import org.lwjgl.opengl.GL11;

public class FXFireParticle implements ParticleTextureSheet {
    public static FXFireParticle INSTANCE = new FXFireParticle();

    @Override
    public void begin(BufferBuilder builder, TextureManager textureManager) {
        MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().enable();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
    }

    @Override
    public void draw(Tessellator tessellator) {
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }
}
