package com.pulsar.soulforge.client.ui;

import com.mojang.authlib.properties.PropertyMap;
import com.pulsar.soulforge.networking.CastWormholePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

public class WormholeScreen extends Screen {
    public static final Identifier TEXTURE = Identifier.of("soulforge","textures/ui/wormhole_menu.png");

    public int x;
    public int y;

    public UUID selectedUUID;

    public WormholeScreen() {
        super(Text.literal("Wormhole"));
    }

    public List<Drawable> widgets = List.of();

    @Override
    protected void init() {
        this.x = (this.width - 176) / 2;
        this.y = (this.height - 98) / 2;
        updateWidgets();
    }

    public void updateWidgets() {
        widgets = new ArrayList<>();
        clearChildren();

        List<UUID> uuids = this.client.getNetworkHandler().getPlayerUuids().stream().toList();

        int i = 0;
        for (UUID uuid : uuids) {
            if (uuid.compareTo(this.client.player.getUuid()) != 0) {
                ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
                skull.set(DataComponentTypes.PROFILE, new ProfileComponent(Optional.empty(), Optional.of(uuid), new PropertyMap()));
                SlotButtonWidget slotWidget = new SlotButtonWidget(skull, x + 62+(i%6)*18, y + 8+(MathHelper.floor(i/6f)*18), () -> {
                    if (selectedUUID == uuid) {
                        ClientPlayNetworking.send(new CastWormholePacket(selectedUUID));
                    }
                    selectedUUID = uuid;
                });
                widgets.add(slotWidget);
                addSelectableChild(slotWidget);
                i++;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(TEXTURE, this.x, this.y, 176, 98, 0, 0, 176, 98, 176, 98);
        for (Drawable widget : widgets) {
            widget.render(context, mouseX, mouseY, delta);
        }
        if (selectedUUID != null) {
            PlayerEntity selected = this.client.player.getEntityWorld().getPlayerByUuid(selectedUUID);
            if (selected != null) {
                float i = x + 31.5f;
                float j = y + 43;
                drawEntity(context, x + 7, y + 8, x + 55, y + 77, 30, 1f, i, j, selected);
            } else {
                updateWidgets();
            }
        }
    }

    public static class SlotButtonWidget extends PressableWidget {
        public final PressAction onPress;
        public final ItemStack stack;

        public SlotButtonWidget(ItemStack stack, int x, int y, PressAction onPress) {
            super(x, y, 18, 18, Text.empty());
            this.onPress = onPress;
            this.stack = stack;
        }

        @Override
        public void onPress() {
            this.onPress.onPress();
        }

        public interface PressAction {
            void onPress();
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            context.drawItem(this.stack, this.getX(), this.getY());
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
    }
}
