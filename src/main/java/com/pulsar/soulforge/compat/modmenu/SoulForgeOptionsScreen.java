package com.pulsar.soulforge.compat.modmenu;

import com.pulsar.soulforge.config.SoulForgeConfig;
import com.terraformersmc.modmenu.config.ModMenuConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class SoulForgeOptionsScreen extends GameOptionsScreen {
    private Screen previous;
    private OptionListWidget list;

    public SoulForgeOptionsScreen(Screen previous) {
        super(previous, MinecraftClient.getInstance().options, Text.translatable("soulforge.options"));
        this.previous = previous;
    }

    @Override
    protected void init() {
        this.list = new OptionListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        this.list.addAll(SoulForgeConfig.asOptions());
        this.addSelectableChild(this.list);
        this.addDrawableChild(
                ButtonWidget.builder(ScreenTexts.DONE, button -> {
                    ModMenuConfigManager.save();
                    this.client.setScreen(this.previous);
                }).position(this.width / 2 - 100, this.height - 27)
                        .size(200, 20)
                        .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
        this.list.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 5, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    public void removed() {
        ModMenuConfigManager.save();
    }
}
