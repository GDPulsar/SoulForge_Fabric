package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class FirstTraitScreen extends Screen {
    private static final Identifier BACKGROUND = new Identifier(SoulForge.MOD_ID, "textures/ui/first_trait.png");
    public final Screen parent;
    public int x;
    public int y;

    public FirstTraitScreen(Screen parent) {
        super(Text.translatable("screen.soulforge.first_trait"));
        this.parent = parent;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        this.x = this.width/2 - 73;
        this.y = this.height/2 - 83;
        SoulScreen.ClickableTextureWidget buttonLeft = new SoulScreen.ClickableTextureWidget(10 + this.x, 10 + this.y, 7, 11, new Identifier(SoulForge.MOD_ID, "textures/ui/button_left.png"), (mouseButton) -> {
            page = (page + 5) % 6;
            scroll = 0;
        }, Text.empty());
        SoulScreen.ClickableTextureWidget buttonRight = new SoulScreen.ClickableTextureWidget(129 + this.x, 10 + this.y, 7, 11, new Identifier(SoulForge.MOD_ID, "textures/ui/button_right.png"), (mouseButton) -> {
            page = (page + 1) % 6;
            scroll = 0;
        }, Text.empty());
        ButtonWidget proceedButton = new ButtonWidget.Builder(Text.translatable("gui.proceed"), button -> {
            ClientPlayNetworking.send(SoulForgeNetworking.FIRST_TRAIT, PacketByteBufs.create().writeVarInt(page));
            close();
        }).dimensions(this.x + 33, this.y + 139, 80, 15).build();
        this.addDrawableChild(buttonLeft);
        this.addDrawableChild(buttonRight);
        this.addDrawableChild(proceedButton);
        this.addSelectableChild(buttonLeft);
        this.addSelectableChild(buttonRight);
        this.addSelectableChild(proceedButton);
    }

    int scroll = 0;
    int page = 0;
    final List<String> translateKeys = List.of(
            "encyclopedia.trait.bravery.text", "encyclopedia.trait.justice.text", "encyclopedia.trait.kindness.text",
            "encyclopedia.trait.patience.text", "encyclopedia.trait.integrity.text", "encyclopedia.trait.perseverance.text");
    final List<TraitBase> traits = List.of(
            Traits.bravery, Traits.justice, Traits.kindness, Traits.patience, Traits.integrity, Traits.perseverance
    );
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(context);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, this.title, this.x + 73, this.y + 10, 0xFFFFFF);
        context.enableScissor(this.x + 8, this.y + 8, this.x + 137, this.y + 157);
        context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, traits.get(page).getLocalizedText(), this.x + 73, this.y + 25, traits.get(page).getColor());
        int i = this.y + 45;
        for (OrderedText text : MinecraftClient.getInstance().textRenderer.wrapLines(Text.translatable(translateKeys.get(page)), 126)) {
            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, text, this.x + 73, i, 0xFFFFFF);
            i += MinecraftClient.getInstance().textRenderer.fontHeight + 4;
        }
        context.disableScissor();
    }

    @Override
    public void renderBackgroundTexture(DrawContext context) {
        context.setShaderColor(0.25f, 0.25f, 0.25f, 1f);
        context.drawTexture(OPTIONS_BACKGROUND_TEXTURE, 0, 0, 0, 0f, 0f, this.width, this.height, 32, 32);
        context.setShaderColor(1f, 1f, 1f, 1f);
        context.drawTexture(BACKGROUND, this.x, this.y, 0, 0f, 0f, 146, 166, 146, 166);
    }
}
