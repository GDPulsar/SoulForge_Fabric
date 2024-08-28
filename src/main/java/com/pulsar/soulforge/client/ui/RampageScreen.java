package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RampageScreen extends Screen {
    protected int guiMiddleX, guiMiddleY;

    public RampageScreen() {
        super(Text.literal("Rampage"));
    }

    @Override
    protected void init() {
        super.init();

        guiMiddleX = this.width / 2;
        guiMiddleY = this.height / 2;
    }

    private int hovering = -1;
    private int selectedStart = -1;
    private int selectedActive = -1;
    private int selectedEnd = -1;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        SoulComponent playerSoul = SoulForgeClient.getPlayerData();
        if (playerSoul != null) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int centerX = guiMiddleX;
            int centerY = guiMiddleY;
            List<TraitBase> traits = new ArrayList<>(Arrays.asList(
                    Traits.bravery,
                    Traits.justice,
                    Traits.kindness,
                    Traits.patience,
                    Traits.integrity,
                    Traits.perseverance
            ));
            double anglePer = (2*Math.PI)/traits.size();
            hovering = -1;
            for (int i = 0; i < traits.size(); i++) {
                ItemStack soul = SoulForgeItems.getSoulItem(traits.get(i), traits.get(i));
                int drawX = (int)(Math.sin(anglePer*i)*100) + centerX;
                int drawY = (int)(Math.cos(anglePer*i)*100) + centerY;
                context.drawTexture(new Identifier(SoulForge.MOD_ID, "textures/ui/slot.png"), drawX-9, drawY-9, 0, 0, 18, 18);
                context.drawItem(soul, drawX-9, drawY-9);
                if (mouseX >= drawX-9 && mouseX < drawX+9 && mouseY >= drawY-9 && mouseY < drawY+9) {
                    hovering = i;
                    context.drawCenteredTextWithShadow(textRenderer, traits.get(i).getLocalizedText(), centerX, centerY, 0xFFFFFF);
                }
            }

            if (selectedStart != -1) context.drawCenteredTextWithShadow(textRenderer, traits.get(selectedStart).getLocalizedText(), centerX - 40, centerY - 40, traits.get(selectedStart).getColor());
            if (selectedActive != -1) context.drawCenteredTextWithShadow(textRenderer, traits.get(selectedActive).getLocalizedText(), centerX, centerY - 40, traits.get(selectedActive).getColor());
            if (selectedEnd != -1) context.drawCenteredTextWithShadow(textRenderer, traits.get(selectedEnd).getLocalizedText(), centerX + 40, centerY - 40, traits.get(selectedEnd).getColor());
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectedStart == -1) {
            selectedStart = hovering;
        } else {
            if (selectedActive == -1) {
                selectedActive = hovering;
            } else {
                if (selectedEnd == -1) {
                    selectedEnd = hovering;
                }
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeVarInt(selectedStart);
                buf.writeVarInt(selectedActive);
                buf.writeVarInt(selectedEnd);
                ClientPlayNetworking.send(SoulForgeNetworking.RAMPAGE_ACTIVATE, buf);
                close();
            }
        }
        return true;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
