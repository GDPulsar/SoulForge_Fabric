package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.SoulForgeClient;
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
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class ReloadScreen extends Screen {
    protected int guiMiddleX, guiMiddleY;

    public ReloadScreen() {
        super(Text.literal("Reload"));
    }

    public static List<Item> getRounds() {
        return List.of(
                SoulForgeItems.FROSTBITE_ROUND,
                SoulForgeItems.CRUSHING_ROUND,
                SoulForgeItems.PUNCTURING_ROUND,
                SoulForgeItems.SUPPRESSING_ROUND
        );
    }

    @Override
    protected void init() {
        super.init();

        guiMiddleX = this.width / 2;
        guiMiddleY = this.height / 2;
    }

    private ItemStack hovering = null;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        SoulComponent playerSoul = SoulForgeClient.getPlayerData();
        if (playerSoul != null) {
            context.fill(0, 0, width, height, 0x77000000);
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int centerX = guiMiddleX;
            int centerY = guiMiddleY;
            int radius = Math.min(this.width, this.height) / 4;
            List<Item> weapons = getRounds();
            double anglePer = (2*Math.PI)/weapons.size();
            hovering = null;
            float mouseAngle = (float)((Math.atan2(mouseX - centerX, mouseY - centerY) + MathHelper.TAU) % MathHelper.TAU);
            for (int i = 0; i < weapons.size(); i++) {
                ItemStack weapon = new ItemStack(weapons.get(i));
                int drawX = (int)(Math.sin(anglePer*i)*radius) + centerX;
                int drawY = (int)(Math.cos(anglePer*i)*radius) + centerY;
                context.getMatrices().push();
                context.getMatrices().translate(drawX, drawY, 0);
                context.getMatrices().scale(2f, 2f, 2f);
                context.drawItem(weapon, -9, -9);
                context.getMatrices().pop();
                if ((anglePer*(i-0.5) < mouseAngle && anglePer*(i+0.5) >= mouseAngle) ||
                        (anglePer*(i-0.5) < mouseAngle - MathHelper.TAU || anglePer*(i+0.5) >= mouseAngle + MathHelper.TAU)) {
                    hovering = weapon;
                    context.drawCenteredTextWithShadow(textRenderer, weapon.getName(), centerX, centerY, 0xFFFFFF);
                }
                float posX = centerX;
                float posY = centerY;
                while (posX >= 0 && posX < width && posY >= 0 && posY < height) {
                    posX += (float)Math.sin(anglePer*(i-0.5));
                    posY += (float)Math.cos(anglePer*(i-0.5));
                    context.fill((int)posX - 1, (int)posY - 1, (int)posX + 1, (int)posY + 1, 0xAA000000);
                }
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (hovering != null) {
            ClientPlayNetworking.send(SoulForgeNetworking.RELOAD_SELECT, PacketByteBufs.create().writeItemStack(hovering));
        }
        super.close();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hovering != null) close();
        return true;
    }
}
