package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.trait.Traits;
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

public class MorphingWeaponryScreen extends Screen {
    protected int guiMiddleX, guiMiddleY;

    public MorphingWeaponryScreen() {
        super(Text.literal("Morphing Weaponry"));
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
                    SoulForgeItems.PERSEVERANCE_BLADES
            ));
            if (playerSoul.getLV() >= 5) weapons.add(SoulForgeItems.PERSEVERANCE_EDGE);
            if (playerSoul.getLV() >= 10) weapons.add(SoulForgeItems.PERSEVERANCE_CLAW);
            if (playerSoul.getLV() >= 17) weapons.add(SoulForgeItems.PERSEVERANCE_HARPOON);
            if ((playerSoul.getLV() >= 12 && playerSoul.isPure()) || playerSoul.getTraits().contains(Traits.spite)) weapons.add(SoulForgeItems.COLOSSAL_CLAYMORE);
            if (playerSoul.getTraits().contains(Traits.integrity) && playerSoul.getTraits().contains(Traits.perseverance)) weapons.add(SoulForgeItems.TRICK_ANCHOR);
            double anglePer = (2*Math.PI)/weapons.size();
            hovering = null;
            for (int i = 0; i < weapons.size(); i++) {
                ItemStack weapon = new ItemStack(weapons.get(i));
                int drawX = (int)(Math.sin(anglePer*i)*100) + centerX;
                int drawY = (int)(Math.cos(anglePer*i)*100) + centerY;
                context.drawTexture(new Identifier(SoulForge.MOD_ID, "textures/ui/slot.png"), drawX-9, drawY-9, 0, 0, 18, 18);
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
            ClientPlayNetworking.send(SoulForgeNetworking.SET_WEAPON, PacketByteBufs.create().writeItemStack(hovering));
        }
        super.close();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hovering != null) close();
        return true;
    }
}
