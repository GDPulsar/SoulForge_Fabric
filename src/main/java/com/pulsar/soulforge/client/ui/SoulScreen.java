package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.data.AbilityLayout;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Constants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class SoulScreen extends Screen {
    public final Screen parent;
    public int x;
    public int y;

    public SoulScreen(Screen parent) {
        super(Text.literal("Soul Screen"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.x = this.width/2 - 101;
        this.y = this.height/2 - 92;
        updateWidgets();
    }

    private AbilityBase selectedAbility = null;

    private Identifier abilityTexture = new Identifier(SoulForge.MOD_ID, "textures/ui/ability_screen.png");
    private Identifier soulTexture = new Identifier(SoulForge.MOD_ID, "textures/ui/soul_screen.png");

    private int page = 0;
    private int modeIndex = 0;
    private List<ClickableWidget> widgets = new ArrayList<>();
    private List<String> modes = new ArrayList<>();

    public void updateWidgets() {
        assert this.client != null;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(this.client.player);

        clearChildren();
        widgets = new ArrayList<>();

        if (page == 0) {
            widgets.add(new SlotWidget(8 + this.x, 6 + this.y, new ItemStack(SoulForgeItems.BRAVERY_HAMMER), () -> {
                page = 0;
                updateWidgets();
            }));
            widgets.add(new SlotWidget(37 + this.x, 9 + this.y, new ItemStack(SoulForgeItems.DETERMINATION_ARNICITE_HEART), () -> {
                page = 1;
                updateWidgets();
            }));
        } else {
            widgets.add(new SlotWidget(8 + this.x, 9 + this.y, new ItemStack(SoulForgeItems.BRAVERY_HAMMER), () -> {
                page = 0;
                updateWidgets();
            }));
            widgets.add(new SlotWidget(37 + this.x, 6 + this.y, new ItemStack(SoulForgeItems.DETERMINATION_ARNICITE_HEART), () -> {
                page = 1;
                updateWidgets();
            }));
        }
        widgets.add(new SlotWidget(176 + this.x, 9 + this.y, new ItemStack(Items.KNOWLEDGE_BOOK), () -> {
            this.client.setScreen(new EncyclopediaScreen());
        }));

        modes = new ArrayList<>();
        if (!playerSoul.hasTrait(Traits.spite)) {
            modes.add(playerSoul.getTrait(0).getName());
            if (playerSoul.getTraitCount() == 2) modes.add(playerSoul.getTrait(1).getName());
        } else {
            for (TraitBase trait : Traits.all()) {
                modes.add(trait.getName());
            }
        }
        boolean hasPassives = false;
        for (AbilityBase ability : playerSoul.getAbilities()) {
            if (ability.getType() == AbilityType.PASSIVE || ability.getType() == AbilityType.PASSIVE_ON_HIT) {
                hasPassives = true;
            }
        }
        if (!Constants.getDualTraitAbilities(playerSoul.getTraits()).isEmpty()) modes.add("Duals");
        if (hasPassives) modes.add("Passives");
        if (page == 0) {
            widgets.add(new ClickableTextureWidget(42 + this.x, 35 + this.y, 7, 11, new Identifier(SoulForge.MOD_ID, "textures/ui/button_left.png"), (mouseButton) -> {
                modeIndex = (modeIndex - 1 + modes.size()) % modes.size();
                updateWidgets();
            }));
            widgets.add(new ClickableTextureWidget(146 + this.x, 35 + this.y, 7, 11, new Identifier(SoulForge.MOD_ID, "textures/ui/button_right.png"), (mouseButton) -> {
                modeIndex = (modeIndex + 1) % modes.size();
                updateWidgets();
            }));
            int i = 0;
            for (AbilityBase ability : Traits.getModeAbilities(modes.get(modeIndex), playerSoul)) {
                Identifier id = new Identifier(SoulForge.MOD_ID, "textures/ui/ability_icon/" + ability.getID().getPath() + ".png");
                ClickableTextureWidget button = new ClickableTextureWidget(17+(i%9)*18 + this.x, 51 + MathHelper.floor(i/9f) * 18 + this.y, 18, 18, id, (mouseButton) -> {
                    selectedAbility = ability;
                    updateWidgets();
                }, ability.getLocalizedText());
                widgets.add(button);
                i++;
            }
            int rowNum = 0;
            for (AbilityLayout.AbilityRow row : playerSoul.getAbilityLayout().rows) {
                int slotNum = 0;
                for (AbilityBase ability : row.abilities) {
                    if (ability != null) {
                        Identifier id = new Identifier(SoulForge.MOD_ID, "textures/ui/ability_icon/" + ability.getID().getPath() + ".png");
                        int finalRow = rowNum;
                        int finalSlot = slotNum;
                        ClickableTextureWidget button = new ClickableTextureWidget(17 + slotNum * 18 + this.x, 101 + rowNum * 18 + this.y, 18, 18, id, (mouseButton) -> {
                            if (selectedAbility != null) {
                                playerSoul.setLayoutAbility(selectedAbility, finalRow, finalSlot);
                                PacketByteBuf buf = PacketByteBufs.create();
                                buf.writeString(selectedAbility.getID().toString());
                                buf.writeVarInt(finalRow);
                                buf.writeVarInt(finalSlot);
                                ClientPlayNetworking.send(SoulForgeNetworking.SET_ABILITY_LAYOUT, buf);
                                updateWidgets();
                            }
                        }, ability.getLocalizedText());
                        widgets.add(button);
                    } else {
                        int finalRow = rowNum;
                        int finalSlot = slotNum;
                        ClickableTextureWidget button = new ClickableTextureWidget(17 + slotNum * 18 + this.x, 101 + rowNum * 18 + this.y, 18, 18, null, (mouseButton) -> {
                            if (selectedAbility != null && mouseButton == 0) {
                                playerSoul.setLayoutAbility(selectedAbility, finalRow, finalSlot);
                                PacketByteBuf buf = PacketByteBufs.create();
                                buf.writeString(selectedAbility.getID().toString());
                                buf.writeVarInt(finalRow);
                                buf.writeVarInt(finalSlot);
                                ClientPlayNetworking.send(SoulForgeNetworking.SET_ABILITY_LAYOUT, buf);
                                updateWidgets();
                            } else if (mouseButton == 1) {
                                playerSoul.setLayoutAbility(null, finalRow, finalSlot);
                                PacketByteBuf buf = PacketByteBufs.create();
                                buf.writeString("null");
                                buf.writeVarInt(finalRow);
                                buf.writeVarInt(finalSlot);
                                ClientPlayNetworking.send(SoulForgeNetworking.SET_ABILITY_LAYOUT, buf);
                                updateWidgets();
                            }
                        });
                        widgets.add(button);
                    }
                    slotNum++;
                }
                rowNum++;
            }
        }

        for (ClickableWidget widget : widgets) {
            addSelectableChild(widget);
            addDrawableChild(widget);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        assert this.client != null;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(this.client.player);
        if (page == 0) {
            context.drawTexture(abilityTexture, this.x, this.y, 201, 184, 0, 0, 201, 184, 201, 184);
            context.drawCenteredTextWithShadow(textRenderer, modes.get(modeIndex), 97 + this.x, 37 + this.y, 0xFFFFFF);
        } else {
            context.drawTexture(soulTexture, this.x, this.y, 201, 184, 0, 0, 201, 184, 201, 184);
            String traitStr = "Trait: " + (playerSoul.getTraitCount() == 2 ? playerSoul.getTrait(0).getName() + "-" + playerSoul.getTrait(1).getName() : playerSoul.getTrait(0).getName());
            context.drawCenteredTextWithShadow(textRenderer, traitStr, 100 + this.x, 50 + this.y, 0xFFFFFF);
            int offset = 60;
            context.drawCenteredTextWithShadow(textRenderer, "LV: " + playerSoul.getLV(), 100 + this.x, offset + this.y, 0xFFFFFF); offset += 10;
            context.drawCenteredTextWithShadow(textRenderer, "EXP: " + playerSoul.getEXP(), 100 + this.x, offset + this.y, 0xFFFFFF); offset += 10;
            if (playerSoul.getLV() < 20) {
                context.drawCenteredTextWithShadow(textRenderer, "EXP until next LV: ", 100 + this.x, offset + this.y, 0xFFFFFF); offset += 10;
                context.drawCenteredTextWithShadow(textRenderer, String.valueOf(playerSoul.getExpRequirement()), 100 + this.x, offset + this.y, 0xFFFFFF); offset += 10;
            }
            context.drawCenteredTextWithShadow(textRenderer, "Power: " + (playerSoul.isPure() || playerSoul.hasTrait(Traits.determination) ? "Pure" : (playerSoul.isStrong() ? "Strong" : "Normal")), 100 + this.x, offset + this.y, 0xFFFFFF);
        }
        for (ClickableWidget widget : widgets) {
            widget.render(context, mouseX, mouseY, delta);
        }
        /*int i = 0;
        for (AbilityBase ability : Traits.getModeAbilities(modes.get(modeIndex), playerSoul)) {
            if (ability == selectedAbility) {
                int x = 17+(i%9)*18 + this.x;
                int y = 51 + MathHelper.floor(i/9f) * 18 + this.y;
                context.drawBorder(x, y, 18, 18, 0xFFFFFF);
                context.drawBorder(x + 1, y + 1, 16, 16, 0xFFFFFF);
            }
            i++;
        }*/
    }

    private int getPageOfAbility(AbilityBase ability) {
        int i = 0;
        for (TraitBase trait : Traits.all()) {
            i++;
            List<AbilityBase> abilities = trait.getAbilities();
            abilities.sort(Comparator.comparingInt(AbilityBase::getLV));
            for (AbilityBase a : abilities) {
                i++;
                if (Objects.equals(a.getName(), ability.getName())) return i;
            }
        }
        return 0;
    }

    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {;
        float widthRatio = this.width / 480f;
        float heightRatio = this.height / 260f;
        return super.mouseClicked(mouseX/widthRatio, mouseY/heightRatio, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {;
        float widthRatio = this.width / 480f;
        float heightRatio = this.height / 260f;
        return super.mouseReleased(mouseX/widthRatio, mouseY/heightRatio, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {;
        float widthRatio = this.width / 480f;
        float heightRatio = this.height / 260f;
        return super.mouseDragged(mouseX/widthRatio, mouseY/heightRatio, button, deltaX/widthRatio, deltaY/heightRatio);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {;
        float widthRatio = this.width / 480f;
        float heightRatio = this.height / 260f;
        return super.mouseScrolled(mouseX/widthRatio, mouseY/heightRatio, horizontalAmount, verticalAmount);
    }*/

    public static class ClickableTextureWidget extends ClickableWidget {
        public Identifier texture;
        public final PressAction pressAction;
        public boolean selected = false;

        public ClickableTextureWidget(int x, int y, int width, int height, Identifier texture, PressAction pressAction, Text tooltip) {
            this(x, y, width, height, texture, pressAction);
            this.setTooltip(Tooltip.of(tooltip));
        }

        public ClickableTextureWidget(int x, int y, int width, int height, Identifier texture, PressAction pressAction) {
            super(x, y, width, height, Text.empty());
            this.texture = texture;
            this.pressAction = pressAction;
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            if (this.texture != null) {
                context.drawTexture(this.texture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
            }
            if (selected) {
                context.drawBorder(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xFFFFFF);
                context.drawBorder(this.getX()+1, this.getY()+1, this.getWidth()-2, this.getHeight()-2, 0xFFFFFF);
            }
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

        public void onClick(int button) {
            if (this.hovered) {
                this.pressAction.onClick(button);
            }
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean bl = this.clicked(mouseX, mouseY);
            if (bl) {
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                this.onClick(button);
                return true;
            }
            return false;
        }

        public interface PressAction {
            void onClick(int button);
        }
    }

    public static class SlotWidget extends ClickableWidget {
        public final ItemStack slotStack;
        public final PressAction pressAction;

        public SlotWidget(int x, int y, ItemStack slotStack, PressAction pressAction) {
            super(x, y, 18, 18, Text.empty());
            this.slotStack = slotStack;
            this.pressAction = pressAction;
            //this.setTooltip(Tooltip.of(slotStack.getName()));
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            context.drawItem(slotStack, this.getX(), this.getY());
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (this.hovered) {
                this.pressAction.onClick();
            }
        }

        public interface PressAction {
            void onClick();
        }
    }
}
