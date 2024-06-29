package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
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
import java.util.List;

public class ReloadScreen extends Screen {
    protected int guiMiddleX, guiMiddleY;

    public ReloadScreen() {
        super(Text.literal("Reload"));
    }

    @Override
    protected void init() {
        super.init();

        guiMiddleX = this.width / 2;
        guiMiddleY = this.height / 2;
    }

    private Item hovering = null;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        SoulComponent playerSoul = SoulForgeClient.getPlayerData();
        if (playerSoul != null) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int centerX = guiMiddleX;
            int centerY = guiMiddleY;
            List<Item> options = new ArrayList<>(List.of(
                    SoulForgeItems.FROSTBITE_ROUND,
                    SoulForgeItems.CRUSHING_ROUND,
                    SoulForgeItems.PUNCTURING_ROUND,
                    SoulForgeItems.SUPPRESSING_ROUND
            ));
            double anglePer = (2*Math.PI)/options.size();
            hovering = null;
            for (int i = 0; i < options.size(); i++) {
                int drawX = (int)(Math.sin(anglePer*i)*100) + centerX;
                int drawY = (int)(Math.cos(anglePer*i)*100) + centerY;
                context.drawTexture(new Identifier(SoulForge.MOD_ID, "textures/ui/slot.png"), drawX-9, drawY-9, 0, 0, 18, 18);
                context.drawItem(new ItemStack(options.get(i)), drawX-8, drawY-8);
                if (mouseX >= drawX-9 && mouseX < drawX+9 && mouseY >= drawY-9 && mouseY < drawY+9) {
                    hovering = options.get(i);
                    context.drawCenteredTextWithShadow(textRenderer, options.get(i).getName(), centerX, centerY, 0xFFFFFF);
                }
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (hovering != null) {
            ClientPlayNetworking.send(SoulForgeNetworking.RELOAD_SELECT, PacketByteBufs.create().writeItemStack(new ItemStack(hovering)));
        }
        super.close();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hovering != null) close();
        return true;
    }
}
