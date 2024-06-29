package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
import com.pulsar.soulforge.ability.Abilities;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.pures.Determine;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetermineScreen extends Screen {
    protected int guiMiddleX, guiMiddleY;
    private int currentTrait;

    public DetermineScreen(int currentTrait) {
        super(Text.literal("Determine"));
        this.currentTrait = currentTrait;
    }

    @Override
    protected void init() {
        super.init();

        guiMiddleX = this.width / 2;
        guiMiddleY = this.height / 2;
    }

    private AbilityBase hovering = null;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        SoulComponent playerSoul = SoulForgeClient.getPlayerData();
        if (playerSoul != null) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int centerX = guiMiddleX;
            int centerY = guiMiddleY;
            List<AbilityBase> options = Determine.getCurrentOptions(currentTrait);
            double anglePer = (2*Math.PI)/options.size();
            hovering = null;
            for (int i = 0; i < options.size(); i++) {
                Identifier identifier = new Identifier(SoulForge.MOD_ID, "textures/ui/ability_icon/" + options.get(i).getID().getPath() + ".png");
                int drawX = (int)(Math.sin(anglePer*i)*100) + centerX;
                int drawY = (int)(Math.cos(anglePer*i)*100) + centerY;
                context.drawTexture(new Identifier(SoulForge.MOD_ID, "textures/ui/slot.png"), drawX-9, drawY-9, 0, 0, 18, 18);
                context.drawTexture(identifier, drawX-9, drawY-9, 18, 18, 0, 0, 66, 66, 66, 66);
                if (mouseX >= drawX-9 && mouseX < drawX+9 && mouseY >= drawY-9 && mouseY < drawY+9) {
                    hovering = options.get(i);
                    context.drawCenteredTextWithShadow(textRenderer, options.get(i).getLocalizedText(), centerX, centerY, 0xFFFFFF);
                }
            }
        }
    }

    @Override
    public void close() {
        if (hovering != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(MinecraftClient.getInstance().player);
            for (AbilityBase ability : playerSoul.getAbilities()) {
                if (ability instanceof Determine determine) {
                    determine.selected = hovering;
                }
            }
            ClientPlayNetworking.send(SoulForgeNetworking.DETERMINE_SELECT, PacketByteBufs.create().writeIdentifier(hovering.getID()));
        }
        super.close();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hovering != null) close();
        return true;
    }
}
