package com.pulsar.soulforge.client.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pulsar.soulforge.SoulForge;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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

        context.enableScissor(x + 150, y + 8, x + 166, y + 77);
        context.drawTexture(LAVA_TEXTURE, x + 150, y + 77 - handler.getScaledLava(), 16, 69, 0, 0, 16, 320, 16, 320);
        context.disableScissor();
        if(handler.isCrafting()) {
            context.drawTexture(TEXTURE, x + 93, y + 50 - handler.getScaledProgress(), 176, 14-handler.getScaledProgress(), 14, handler.getScaledProgress());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        if (mouseX >= x + 150 && mouseX <= x + 166 && mouseY >= y + 8 & mouseY <= y + 77) {
            setTooltip(Text.translatable("soulforge.soul_forge_screen.lava_prefix").append(handler.getLava() + " mB"));
        }
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
