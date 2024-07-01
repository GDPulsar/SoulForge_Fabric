package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.networking.SetWeaponPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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

public class WeaponWheelScreen extends Screen {
    protected int guiMiddleX, guiMiddleY;

    public WeaponWheelScreen() {
        super(Text.literal("Weapon Wheel"));
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
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int centerX = guiMiddleX;
            int centerY = guiMiddleY;
            List<Item> weapons = new ArrayList<>(Arrays.asList(
                    SoulForgeItems.REAL_KNIFE,
                    SoulForgeItems.DETERMINATION_GREATSWORD,
                    SoulForgeItems.DETERMINATION_SPEAR,
                    SoulForgeItems.DETERMINATION_HAMMER,
                    SoulForgeItems.DETERMINATION_BOW,
                    SoulForgeItems.DETERMINATION_RAPIER,
                    SoulForgeItems.DETERMINATION_BLADES,
                    SoulForgeItems.DETERMINATION_SWORD,
                    SoulForgeItems.DETERMINATION_HARPOON,
                    SoulForgeItems.DETERMINATION_CROSSBOW,
                    SoulForgeItems.DETERMINATION_GUN,
                    SoulForgeItems.DETERMINATION_GAUNTLETS
            ));
            double anglePer = (2*Math.PI)/weapons.size();
            hovering = null;
            for (int i = 0; i < weapons.size(); i++) {
                ItemStack weapon = new ItemStack(weapons.get(i));
                int drawX = (int)(Math.sin(anglePer*i)*100) + centerX;
                int drawY = (int)(Math.cos(anglePer*i)*100) + centerY;
                context.drawTexture(Identifier.of(SoulForge.MOD_ID, "textures/ui/slot.png"), drawX-9, drawY-9, 0, 0, 18, 18);
                context.drawItem(weapon, drawX-9, drawY-9);
                if (mouseX >= drawX-9 && mouseX < drawX+9 && mouseY >= drawY-9 && mouseY < drawY+9) {
                    hovering = weapon;
                    context.drawCenteredTextWithShadow(textRenderer, weapon.getName(), centerX, centerY, 0xFFFFFF);
                }
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (hovering != null) {
            ClientPlayNetworking.send(new SetWeaponPacket(hovering));
        }
        super.close();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hovering != null) close();
        return true;
    }
}
