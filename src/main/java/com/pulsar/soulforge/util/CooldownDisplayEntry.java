package com.pulsar.soulforge.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.awt.*;

public class CooldownDisplayEntry {
    public Identifier id;
    public String name;
    public float minimum;
    public float value;
    public float maximum;
    public Color color;

    public CooldownDisplayEntry(Identifier id, String name, float min, float val, float max, Color color) {
        this.id = id;
        this.name = name;
        this.minimum = min;
        this.value = val;
        this.maximum = max;
        this.color = color;
    }

    public float getPercent() {
        return (value - minimum) / (maximum - minimum);
    }

    public void render(DrawContext context, int centerX, int centerY, int scale) {
        for (int x = -scale - 4; x <= scale + 4; x++) {
            for (int y = -scale - 4; y <= scale + 4; y++) {
                if (x * x + y * y <= scale * scale) {
                    if (Math.atan2(x, y) < Math.PI * (getPercent() * 2f - 1f)) {
                        context.fill(x + centerX, y + centerY, x + centerX + 1, y + centerY + 1, color.getRGB());
                    } else {
                        context.fill(x + centerX, y + centerY, x + centerX + 1, y + centerY + 1, color.darker().getRGB());
                    }
                } else if (x * x + y * y <= (scale + 2) * (scale + 2)) {
                    context.fill(x + centerX, y + centerY, x + centerX + 1, y + centerY + 1, 0xFF000000);
                }
            }
        }
        context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, name, centerX, centerY - scale - 14, 0xFFFFFF);
    }

    public void render(DrawContext context, int centerX, int centerY) {
        render(context, centerX, centerY, 25);
    }
}
