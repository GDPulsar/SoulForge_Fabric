package com.pulsar.soulforge.client.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SoulForgeScreen extends HandledScreen<SoulForgeScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/ui/soul_forge.png");
    private static final Identifier LAVA_TEXTURE = new Identifier("minecraft", "textures/block/lava_still.png");

    public SoulForgeScreen(SoulForgeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        titleY = 1000;
        playerInventoryTitleY = 1000;
        x = (width - backgroundWidth) / 2;
        y = (height - backgroundHeight) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        context.drawTexture(LAVA_TEXTURE, x + 150, y + 77 - handler.getScaledLava(), 16, handler.getScaledLava(), 0, 0, 16, 320, 16, 320);
        if(handler.isCrafting()) {
            context.drawTexture(TEXTURE, x + 93, y + 50 - handler.getScaledProgress(), 176, 14-handler.getScaledProgress(), 14, handler.getScaledProgress());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
