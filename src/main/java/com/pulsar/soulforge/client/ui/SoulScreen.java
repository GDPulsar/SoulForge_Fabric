package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.WorldComponent;
import com.pulsar.soulforge.data.AbilityLayout;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Constants;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import java.util.*;

import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

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
    private EntityType<?> selectedEntity = null;

    private final Identifier abilityTexture = new Identifier(SoulForge.MOD_ID, "textures/ui/ability_screen.png");
    private final Identifier soulTexture = new Identifier(SoulForge.MOD_ID, "textures/ui/soul_screen.png");
    private final Identifier killsTexture = new Identifier(SoulForge.MOD_ID, "textures/ui/kills_screen.png");

    private int page = 0;
    private int modeIndex = 0;
    private List<ClickableWidget> widgets = new ArrayList<>();
    private List<Pair<Traits.Mode, String>> modes = new ArrayList<>();

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
            widgets.add(new SlotWidget(66 + this.x, 9 + this.y, new ItemStack(Items.ZOMBIE_HEAD), () -> {
                page = 2;
                updateWidgets();
            }));
        } else if (page == 1) {
            widgets.add(new SlotWidget(8 + this.x, 9 + this.y, new ItemStack(SoulForgeItems.BRAVERY_HAMMER), () -> {
                page = 0;
                updateWidgets();
            }));
            widgets.add(new SlotWidget(37 + this.x, 6 + this.y, new ItemStack(SoulForgeItems.DETERMINATION_ARNICITE_HEART), () -> {
                page = 1;
                updateWidgets();
            }));
            widgets.add(new SlotWidget(66 + this.x, 9 + this.y, new ItemStack(Items.ZOMBIE_HEAD), () -> {
                page = 2;
                updateWidgets();
            }));
        } else if (page == 2) {
            widgets.add(new SlotWidget(8 + this.x, 9 + this.y, new ItemStack(SoulForgeItems.BRAVERY_HAMMER), () -> {
                page = 0;
                updateWidgets();
            }));
            widgets.add(new SlotWidget(37 + this.x, 9 + this.y, new ItemStack(SoulForgeItems.DETERMINATION_ARNICITE_HEART), () -> {
                page = 1;
                updateWidgets();
            }));
            widgets.add(new SlotWidget(66 + this.x, 6 + this.y, new ItemStack(Items.ZOMBIE_HEAD), () -> {
                page = 2;
                updateWidgets();
            }));
        }

        modes = new ArrayList<>();
        if (!playerSoul.hasTrait(Traits.spite)) {
            modes.add(new Pair<>(Traits.Mode.TRAIT, playerSoul.getTrait(0).getName()));
            if (playerSoul.getTraitCount() == 2) modes.add(new Pair<>(Traits.Mode.TRAIT, playerSoul.getTrait(1).getName()));
        } else {
            for (TraitBase trait : Traits.all()) {
                modes.add(new Pair<>(Traits.Mode.TRAIT, trait.getName()));
            }
        }
        boolean hasPassives = false;
        for (AbilityBase ability : playerSoul.getAbilities()) {
            if (ability.getType() == AbilityType.PASSIVE) {
                hasPassives = true;
            }
        }
        if (!Constants.getDualTraitAbilities(playerSoul.getTraits()).isEmpty()) modes.add(new Pair<>(Traits.Mode.DUALS, ""));
        if (hasPassives) modes.add(new Pair<>(Traits.Mode.PASSIVES, ""));
        if (Utils.hasHate(client.player)) modes.add(new Pair<>(Traits.Mode.HATE, ""));
        if (playerSoul.hasTrait(Traits.spite)) modes.add(new Pair<>(Traits.Mode.SPECIAL, ""));
        if (page == 0) {
            widgets.add(new ClickableTextureWidget(42 + this.x, 35 + this.y, 7, 11, new Identifier(SoulForge.MOD_ID, "textures/ui/button_left.png"), (mouseButton) -> {
                modeIndex = (modeIndex - 1 + modes.size()) % modes.size();
                updateWidgets();
            }, Text.literal(modes.get((modeIndex - 1 + modes.size()) % modes.size()).getRight())));
            widgets.add(new ClickableTextureWidget(146 + this.x, 35 + this.y, 7, 11, new Identifier(SoulForge.MOD_ID, "textures/ui/button_right.png"), (mouseButton) -> {
                modeIndex = (modeIndex + 1) % modes.size();
                updateWidgets();
            }, Text.literal(modes.get((modeIndex + 1) % modes.size()).getRight())));
            int i = 0;
            for (AbilityBase ability : Traits.getModeAbilities(modes.get(modeIndex).getLeft(), modes.get(modeIndex).getRight(), playerSoul)) {
                Identifier id;
                if (ability instanceof ToggleableAbilityBase) id = new Identifier(ability.getID().getNamespace(), "textures/ui/ability_icon/" + ability.getID().getPath() + "_on.png");
                else id = new Identifier(SoulForge.MOD_ID, "textures/ui/ability_icon/" + ability.getID().getPath() + ".png");
                ClickableTextureWidget button = new ClickableTextureWidget(17+(i%9)*18 + this.x, 51 + MathHelper.floor(i/9f) * 18 + this.y, 18, 18, id, (mouseButton) -> {
                    selectedAbility = ability;
                    updateWidgets();
                }, ability.getLocalizedText());
                if (selectedAbility != null) {
                    if (ability.getID() == selectedAbility.getID()) {
                        button.selected = true;
                    }
                }
                widgets.add(button);
                i++;
            }
            int rowNum = 0;
            for (AbilityLayout.AbilityRow row : playerSoul.getAbilityLayout().rows) {
                int slotNum = 0;
                for (AbilityBase ability : row.abilities) {
                    if (ability != null) {
                        Identifier id;
                        if (ability instanceof ToggleableAbilityBase) id = new Identifier(ability.getID().getNamespace(), "textures/ui/ability_icon/" + ability.getID().getPath() + "_on.png");
                        else id = new Identifier(SoulForge.MOD_ID, "textures/ui/ability_icon/" + ability.getID().getPath() + ".png");
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
        } else if (page == 2) {
            ButtonListWidget buttonList = new ButtonListWidget(MinecraftClient.getInstance(), x + 9, y + 43, 40, 130, 14);
            int i = 0;
            for (Map.Entry<String, Integer> entry : playerSoul.getMonsterSouls().entrySet()) {
                Identifier typeId = Identifier.tryParse(entry.getKey());
                if (Registries.ENTITY_TYPE.containsId(typeId)) {
                    EntityType<?> entityType = Registries.ENTITY_TYPE.get(typeId);
                    ButtonWidget buttonWidget = ButtonWidget.builder(
                            Text.translatable(entityType.getTranslationKey()),
                            button -> {
                                selectedEntity = entityType;
                                updateWidgets();
                            }
                    ).dimensions(x + 9, y + 43 + 14 * i, 40, 14).build();
                    buttonList.addButton(buttonWidget);
                    i++;
                }
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
            context.drawCenteredTextWithShadow(textRenderer, modes.get(modeIndex).getLeft() == Traits.Mode.TRAIT ? modes.get(modeIndex).getRight() : modes.get(modeIndex).getLeft().name(), 97 + this.x, 37 + this.y, 0xFFFFFF);
        } else if (page == 1) {
            context.drawTexture(soulTexture, this.x, this.y, 201, 184, 0, 0, 201, 184, 201, 184);
            context.drawCenteredTextWithShadow(textRenderer, Text.literal("Trait: ").append(Utils.getTraitText(playerSoul)), 100 + this.x, 50 + this.y, 0xFFFFFF);
            int offset = 60;
            context.drawCenteredTextWithShadow(textRenderer, "LV: " + playerSoul.getLV(), 100 + this.x, offset + this.y, 0xFFFFFF); offset += 10;
            context.drawCenteredTextWithShadow(textRenderer, "EXP: " + playerSoul.getEXP(), 100 + this.x, offset + this.y, 0xFFFFFF); offset += 10;
            if (playerSoul.getLV() < 20) {
                context.drawCenteredTextWithShadow(textRenderer, "EXP until next LV: ", 100 + this.x, offset + this.y, 0xFFFFFF); offset += 10;
                context.drawCenteredTextWithShadow(textRenderer, String.valueOf(playerSoul.getExpRequirement()), 100 + this.x, offset + this.y, 0xFFFFFF); offset += 10;
            }
            context.drawCenteredTextWithShadow(textRenderer, "Power: " + (playerSoul.isPure() || playerSoul.hasTrait(Traits.determination) ? "Pure" : (playerSoul.isStrong() ? "Strong" : "Normal")), 100 + this.x, offset + this.y, 0xFFFFFF); offset += 10;
            context.drawCenteredTextWithShadow(textRenderer, playerSoul.getAbilities().size() + " Total Abilities", 100 + this.x, offset + this.y, 0xFFFFFF); offset += 10;
            context.drawCenteredTextWithShadow(textRenderer, "Magic Maximum: " + playerSoul.getMagicMax(), 100 + this.x, offset + this.y, 0xFFFFFF); offset += 10;
            if (Utils.isInverted(playerSoul)) {
                context.drawCenteredTextWithShadow(textRenderer, "Reserves: " + playerSoul.getMagicGauge() + " / " + playerSoul.getMagicGaugeMax(), 100 + this.x, offset + this.y, 0xFFFFFF); offset += 10;
            }
            int totalMonsterSouls = playerSoul.getMonsterSouls().values().stream().mapToInt(a -> a).sum();
            int totalPlayerSouls = playerSoul.getPlayerSouls().values().stream().mapToInt(a -> a).sum();
            context.drawCenteredTextWithShadow(textRenderer, "Monster Souls: " + totalMonsterSouls, 100 + this.x, offset + this.y, 0xFFFFFF); offset += 10;
            context.drawCenteredTextWithShadow(textRenderer, "Player Souls: " + totalPlayerSouls, 100 + this.x, offset + this.y, 0xFFFFFF); offset += 10;
        } else if (page == 2) {
            context.drawTexture(killsTexture, this.x, this.y, 201, 184, 0, 0, 201, 184, 201, 184);
            if (selectedEntity != null) {
                Entity drawnEntity = selectedEntity.create(this.client.world);
                if (drawnEntity instanceof LivingEntity living) {
                    float i = x + 140;
                    float j = y + 60;
                    drawEntity(context, x + 140, y + 45, 30, i, j, living);
                    context.drawCenteredTextWithShadow(textRenderer, selectedAbility.getLocalizedText(), x + 97, y + 44, 0xFFFFFF);
                    WorldComponent worldComponent = SoulForge.getWorldComponent(client.world);
                    float damageExpMultiplier = Utils.getEntityExpMultiplier(living) * worldComponent.getExpMultiplier() * Utils.getPlayerKillCountExpMultiplier(living, client.player);
                    context.drawCenteredTextWithShadow(textRenderer, "EXP Multiplier: " + String.format("%.02f ", damageExpMultiplier), x + 97, y + 56, 0xFFFFFF);
                    context.drawCenteredTextWithShadow(textRenderer, "Kill EXP: " + Utils.getKillExp(living, client.player), x + 97, y + 68, 0xFFFFFF);
                }
            }
        }

        for (ClickableWidget widget : widgets) {
            widget.render(context, mouseX, mouseY, delta);
        }
        int i = 0;
        if (selectedAbility != null && page == 0) {
            for (AbilityBase ability : Traits.getModeAbilities(modes.get(modeIndex).getLeft(), modes.get(modeIndex).getRight(), playerSoul)) {
                if (ability.getID().equals(selectedAbility.getID())) {
                    int x = 17 + (i % 9) * 18 + this.x;
                    int y = 51 + MathHelper.floor(i / 9f) * 18 + this.y;
                    context.drawBorder(x, y, 18, 18, 0xFF00FF00);
                    context.drawBorder(x + 1, y + 1, 16, 16, 0xFF00FF00);
                }
                i++;
            }
        }
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

    public static class ClickableTextureWidget extends ClickableWidget {
        public Identifier texture = null;
        public Identifier hoveredTexture = null;
        public final PressAction pressAction;
        public boolean selected = false;

        public ClickableTextureWidget(int x, int y, int width, int height, Identifier texture, Identifier hoveredTexture, PressAction pressAction, Text tooltip) {
            this(x, y, width, height, texture, hoveredTexture, pressAction);
            this.setTooltip(Tooltip.of(tooltip));
        }

        public ClickableTextureWidget(int x, int y, int width, int height, Identifier texture, PressAction pressAction, Text tooltip) {
            this(x, y, width, height, texture, pressAction);
            this.setTooltip(Tooltip.of(tooltip));
        }

        public ClickableTextureWidget(int x, int y, int width, int height, Identifier texture, Identifier hoveredTexture, PressAction pressAction) {
            this(x, y, width, height, texture, pressAction);
            this.hoveredTexture = hoveredTexture;
        }

        public ClickableTextureWidget(int x, int y, int width, int height, Identifier texture, PressAction pressAction) {
            super(x, y, width, height, Text.empty());
            this.texture = texture;
            this.pressAction = pressAction;
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            if (this.texture != null && !this.hovered) {
                context.drawTexture(this.texture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
            } else if (this.hovered) {
                if (this.hoveredTexture == null && this.texture != null) {
                    context.drawTexture(this.texture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
                } else if (this.hoveredTexture != null) {
                    context.drawTexture(this.hoveredTexture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
                }
            }
            if (selected) {
                context.fill(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0);
                context.drawBorder(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0);
                context.drawBorder(this.getX()+1, this.getY()+1, this.getWidth()-2, this.getHeight()-2, 0);
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

    public static class ButtonListWidget extends EntryListWidget<ButtonListWidget.ButtonEntry> {
        public ButtonListWidget(MinecraftClient client, int left, int top, int width, int height, int itemHeight) {
            super(client, width, height, top, top + height, itemHeight);
            //this.left = left;
            //this.right = left + width;
            SoulForge.LOGGER.info("top: {}, bottom: {}, left: {}, right: {}", this.top, this.bottom, this.left, this.right);
        }

        public void addButton(ButtonWidget button) {
            this.addEntry(new ButtonEntry(button));
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {}

        public static class ButtonEntry extends EntryListWidget.Entry<ButtonEntry> {
            private final ButtonWidget buttonWidget;

            public ButtonEntry(ButtonWidget buttonWidget) {
                this.buttonWidget = buttonWidget;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                //this.buttonWidget.setPosition(x, y);
                this.buttonWidget.render(context, mouseX, mouseY, tickDelta);
                SoulForge.LOGGER.info("button x: {}, button y: {}", this.buttonWidget.getX(), this.buttonWidget.getY());
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                return this.buttonWidget.mouseClicked(mouseX, mouseY, button);
            }
        }
    }
}
