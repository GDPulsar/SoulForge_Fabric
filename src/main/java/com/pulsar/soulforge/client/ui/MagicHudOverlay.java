package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.config.SoulForgeConfig;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.awt.*;

public class MagicHudOverlay implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        //renderMagicBALLS(context);
        renderMagicBar(context);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(MinecraftClient.getInstance().player);
        if (Utils.isInverted(playerSoul)) {
            renderMagicGauge(context);
        }
    }

    int rainbowAnim = 0;

    public void renderMagicBar(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        int top;
        int left;
        switch (SoulForgeConfig.MAGIC_BAR_LOCATION.getValue()) {
            case BOTTOM_RIGHT -> {
                top = client.getWindow().getScaledHeight() - 136;
                left = client.getWindow().getScaledWidth() - 27;
            }
            case TOP_LEFT -> {
                top = 30;
                left = 5;
            }
            case TOP_RIGHT -> {
                top = 30;
                left = client.getWindow().getScaledWidth() - 27;
            }
            default -> {
                top = client.getWindow().getScaledHeight() - 136;
                left = 5;
            }
        }
        SoulComponent playerSoul = SoulForge.getPlayerSoul(client.player);
        context.drawTexture(new Identifier(SoulForge.MOD_ID, "textures/ui/magic/bar_background.png"), left, top, 0, 0, 21, 106, 21, 106);
        int magic = (int) playerSoul.getMagic();
        int colorA = playerSoul.getTrait(0).getColor();
        int colorB = playerSoul.getTrait(playerSoul.getTraitCount()-1).getColor();
        colorA = new Color(colorA, false).getRGB();
        colorB = new Color(colorB, false).getRGB();
        int darkA = new Color(colorA).darker().getRGB();
        int darkB = new Color(colorB).darker().getRGB();
        int darkkA = new Color(darkA).darker().getRGB();
        int darkkB = new Color(darkB).darker().getRGB();
        Color styleColor = new Color(255, 0, 255);

        context.drawVerticalLine(left + 2, top + 2, top + 52, darkkB);
        context.drawVerticalLine(left + 2, top + 51, top + 103, darkkA);
        context.drawVerticalLine(left + 13, top + 2, top + 52, darkkB);
        context.drawVerticalLine(left + 13, top + 51, top + 103, darkkA);
        context.drawVerticalLine(left + 18, top + 2, top + 52, darkkB);
        context.drawVerticalLine(left + 18, top + 51, top + 103, darkkA);
        context.drawHorizontalLine(left + 3, left + 18, top + 2, darkkB);
        context.drawHorizontalLine(left + 3, left + 18, top + 103, darkkA);
        if (magic != 0) context.fill(left + 3, top + 102 - Math.min(magic - 1, 50), left + 13, top + 103, colorA);
        if (magic <= 50) context.fill(left + 3, top + 103 - (magic - 1), left + 13, top + 52, darkA);
        else context.fill(left + 3, top + 102 - (magic - 1), left + 13, top + 52, colorB);
        if (magic != 100) context.fill(left + 3, top + 102 - Math.max(50, magic - 1), left + 13, top + 2, darkB);
        float stylePercent = (float)playerSoul.getStyle() / (float)playerSoul.getStyleRequirement();
        context.fill(left + 14, top + 103, left + 18, top + 103 - (int)(stylePercent * 100), styleColor.getRGB());
        context.fill(left + 14, top + 103 - (int)(stylePercent * 100), left + 18, top + 3, styleColor.darker().getRGB());
        if (playerSoul.getStyleRank() < 5) {
            Identifier styleTextureID = new Identifier(SoulForge.MOD_ID, "textures/ui/magic/style_" + playerSoul.getStyleRank() + ".png");
            context.drawTexture(styleTextureID, left + 3, top - 20, 0, 0, 15,15, 15,15);
        } else if (playerSoul.getStyleRank() == 5) {
            for (int x = 0; x < 15; x++) {
                for (int y = 0; y < 15; y++) {
                    context.fill(left + x + 3, top + y - 20, left + x + 4, top + y - 19, Color.getHSBColor(((rainbowAnim + x + y)%40)/40f, 1f, 1f).getRGB());
                }
            }
            rainbowAnim = (rainbowAnim + 1) % 40;
            Identifier styleTextureID = new Identifier(SoulForge.MOD_ID, "textures/ui/magic/style_5.png");
            context.drawTexture(styleTextureID, left + 3, top - 20, 0, 0, 15,15, 15,15);
        } else {
            Identifier styleTextureID = new Identifier(SoulForge.MOD_ID, "textures/ui/forg.png");
            context.drawTexture(styleTextureID, left + 3, top - 20, 0, 0, 15,15, 15,15);
        }
    }

    public void renderMagicGauge(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        int top;
        int left;
        switch (SoulForgeConfig.MAGIC_BAR_LOCATION.getValue()) {
            case BOTTOM_RIGHT -> {
                top = client.getWindow().getScaledHeight() - 136;
                left = client.getWindow().getScaledWidth() - 67;
            }
            case TOP_LEFT -> {
                top = 30;
                left = 55;
            }
            case TOP_RIGHT -> {
                top = 30;
                left = client.getWindow().getScaledWidth() - 67;
            }
            default -> {
                top = client.getWindow().getScaledHeight() - 136;
                left = 55;
            }
        }
        SoulComponent playerSoul = SoulForge.getPlayerSoul(client.player);
        context.drawTexture(new Identifier(SoulForge.MOD_ID, "textures/ui/magic/bar_background.png"), left, top, 0, 0, 21, 106, 21, 106);
        int magic = (int)(playerSoul.getMagicGauge() / 300);
        int colorA = playerSoul.getTrait(0).getColor();
        int colorB = playerSoul.getTrait(playerSoul.getTraitCount()-1).getColor();
        colorA = new Color(colorA, false).getRGB();
        colorB = new Color(colorB, false).getRGB();
        int darkA = new Color(colorA).darker().getRGB();
        int darkB = new Color(colorB).darker().getRGB();
        int darkkA = new Color(darkA).darker().getRGB();
        int darkkB = new Color(darkB).darker().getRGB();
        Color styleColor = new Color(255, 0, 255);

        context.drawVerticalLine(left + 2, top + 2, top + 52, darkkB);
        context.drawVerticalLine(left + 2, top + 51, top + 103, darkkA);
        context.drawVerticalLine(left + 13, top + 2, top + 52, darkkB);
        context.drawVerticalLine(left + 13, top + 51, top + 103, darkkA);
        context.drawVerticalLine(left + 18, top + 2, top + 52, darkkB);
        context.drawVerticalLine(left + 18, top + 51, top + 103, darkkA);
        context.drawHorizontalLine(left + 3, left + 18, top + 2, darkkB);
        context.drawHorizontalLine(left + 3, left + 18, top + 103, darkkA);
        if (magic != 0) context.fill(left + 3, top + 102 - Math.min(magic - 1, 50), left + 13, top + 103, colorA);
        if (magic <= 50) context.fill(left + 3, top + 103 - (magic - 1), left + 13, top + 52, darkA);
        else context.fill(left + 3, top + 102 - (magic - 1), left + 13, top + 52, colorB);
        if (magic != 100) context.fill(left + 3, top + 102 - Math.max(50, magic - 1), left + 13, top + 2, darkB);
        float stylePercent = (float)playerSoul.getStyle() / (float)playerSoul.getStyleRequirement();
        context.fill(left + 14, top + 103, left + 18, top + 103 - (int)(stylePercent * 100), styleColor.getRGB());
        context.fill(left + 14, top + 103 - (int)(stylePercent * 100), left + 18, top + 3, styleColor.darker().getRGB());
        if (playerSoul.getStyleRank() < 5) {
            Identifier styleTextureID = new Identifier(SoulForge.MOD_ID, "textures/ui/magic/style_" + playerSoul.getStyleRank() + ".png");
            context.drawTexture(styleTextureID, left + 3, top - 20, 0, 0, 15,15, 15,15);
        } else if (playerSoul.getStyleRank() == 5) {
            for (int x = 0; x < 15; x++) {
                for (int y = 0; y < 15; y++) {
                    context.fill(left + x + 3, top + y - 20, left + x + 4, top + y - 19, Color.getHSBColor(((rainbowAnim + x + y)%40)/40f, 1f, 1f).getRGB());
                }
            }
            rainbowAnim = (rainbowAnim + 1) % 40;
            Identifier styleTextureID = new Identifier(SoulForge.MOD_ID, "textures/ui/magic/style_5.png");
            context.drawTexture(styleTextureID, left + 3, top - 20, 0, 0, 15,15, 15,15);
        } else {
            Identifier styleTextureID = new Identifier(SoulForge.MOD_ID, "textures/ui/forg.png");
            context.drawTexture(styleTextureID, left + 3, top - 20, 0, 0, 15,15, 15,15);
        }
    }

    public void renderMagicBALLS(DrawContext context) {
        int y;
        MinecraftClient client = MinecraftClient.getInstance();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(client.player);
        int magic = (int) playerSoul.getMagic();

        y = client.getWindow().getScaledHeight();

        for (int i = 0; i < 10; i++) {
            String traitName;
            if (playerSoul.getTraitCount() == 1) traitName = playerSoul.getTrait(0).getName().toLowerCase();
            else traitName = (i < 5 ? playerSoul.getTrait(0) : playerSoul.getTrait(1)).getName().toLowerCase();
            if (magic < (i + 1) * 10 && magic >= i * 10) {
                Identifier texture = new Identifier(SoulForge.MOD_ID, "textures/ui/magic/" + traitName + "/magic_" + (magic % 10) + ".png");
                context.drawTexture(texture, 5, y - 30 - (i * 14), 0, 0, 12, 12, 12, 12);
            } else if (magic < (i + 1) * 10) {
                Identifier texture = new Identifier(SoulForge.MOD_ID, "textures/ui/magic/" + traitName + "/magic_0.png");
                context.drawTexture(texture, 5, y - 30 - (i * 14), 0, 0, 12, 12, 12, 12);
            } else {
                Identifier texture = new Identifier(SoulForge.MOD_ID, "textures/ui/magic/" + traitName + "/magic_10.png");
                context.drawTexture(texture, 5, y - 30 - (i * 14), 0, 0, 12, 12, 12, 12);
            }
        }
    }
}
