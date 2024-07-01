package com.pulsar.soulforge.client.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pulsar.soulforge.SoulForge;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class CreativeZoneScreen extends HandledScreen<CreativeZoneScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(SoulForge.MOD_ID, "textures/ui/creative_zone.png");

    public CreativeZoneScreen(CreativeZoneScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleY = 2000;
        playerInventoryTitleY = 2000;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
        context.drawCenteredTextWithShadow(this.textRenderer, getFuelText(), this.width/2, y + 20, 0xFFFFFF);
    }

    private MutableText getFuelText() {
        MutableText text = Text.translatable("soulforge.creative_zone_screen.fuel_prefix");
        int seconds = MathHelper.floor(this.handler.getFuel()/20f)%60;
        int minutes = MathHelper.floor(this.handler.getFuel()/1200f)%60;
        int hours = MathHelper.floor(this.handler.getFuel()/72000f);
        if (hours > 0) {
            text.append(hours + "h ");
            text.append(minutes + "m ");
            text.append(seconds + "s");
        } else if (minutes > 0) {
            text.append(minutes + "m ");
            text.append(seconds + "s");
        } else {
            text.append(seconds + "s");
        }
        return text;
    }
}
