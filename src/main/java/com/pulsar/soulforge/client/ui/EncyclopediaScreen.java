package com.pulsar.soulforge.client.ui;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.StringCalculator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

@Environment(EnvType.CLIENT)
public class EncyclopediaScreen extends Screen {
    public static final Identifier TEXTURE = new Identifier("soulforge","textures/ui/encyclopedia.png");


    public EncyclopediaScreen() {
        super(Text.literal("Encyclopedia"));
    }

    public EncyclopediaScreen(int pageNum) {
        super(Text.literal("Encyclopedia"));
        this.currentPage = pageNum;
    }

    public PageButton leftPage;
    public PageButton rightPage;

    public int currentPage = 0;
    public int maxPages = 101;
    public List<Drawable> widgets = List.of();

    @Override
    protected void init() {
        updateWidgets();
    }

    public void updateWidgets() {
        widgets = new ArrayList<>();
        clearChildren();
        leftPage = new PageButton(width / 2 - 125, height / 2 + 64, true, () -> {
            currentPage = Math.max(0, currentPage - 1);
            updateWidgets();
        });
        rightPage = new PageButton(width / 2 + 102, height / 2 + 64, false, () -> {
            currentPage = Math.min(maxPages, currentPage + 1);
            updateWidgets();
        });

        widgets.add(leftPage);
        widgets.add(rightPage);
        addSelectableChild(leftPage);
        addSelectableChild(rightPage);

        SoulComponent playerSoul = SoulForge.getPlayerSoul(this.client.player);
        int left = (width - 283)/2;
        int top = (height - 180)/2;
        if (currentPage == 0) {
            int i = 1, j = 0;
            for (TraitBase trait : Traits.all()) {
                int finalI = i;
                ContentButton traitButton = new ContentButton(left + 159, top + 17 + j * 10, 105, 9, () -> {
                    currentPage = finalI;
                    updateWidgets();
                }, trait.getName());
                i += trait.getAbilities().size() + 1;
                j++;
                widgets.add(traitButton);
                addSelectableChild(traitButton);
            }
            ContentButton siphonsButton = new ContentButton(left + 159, top + 17 + j * 10, 105, 9, () -> {
                currentPage = 77;
                updateWidgets();
            }, "Siphons");
            widgets.add(siphonsButton);
            addSelectableChild(siphonsButton);
            j++;
            ContentButton dualsButton = new ContentButton(left + 159, top + 17 + j * 10, 105, 9, () -> {
                currentPage = 81;
                updateWidgets();
            }, "Duals");
            widgets.add(dualsButton);
            addSelectableChild(dualsButton);
            j++;
            ContentButton puresButton = new ContentButton(left + 159, top + 17 + j * 10, 105, 9, () -> {
                currentPage = 96;
                updateWidgets();
            }, "Pures");
            widgets.add(puresButton);
            addSelectableChild(puresButton);
        }
        int i = 1;
        for (TraitBase trait : Traits.all()) {
            if (currentPage == i) {
                int j = 0;
                List<AbilityBase> abilities = List.copyOf(trait.getAbilities());
                abilities = abilities.stream().sorted(Comparator.comparingInt(AbilityBase::getLV)).toList();
                for (AbilityBase ability : abilities) {
                    int pageNum = i + j;
                    ContentButton abilityButton = new ContentButton(left + 159, top + 17 + j * 10, 105, 9, () -> {
                        currentPage = pageNum + 1;
                        updateWidgets();
                    }, playerSoul.hasDiscovered(ability) ? ability.getLocalizedText() : Text.literal("???"));
                    j++;
                    widgets.add(abilityButton);
                    addSelectableChild(abilityButton);
                }
            }
            i += trait.getAbilities().size() + 1;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        int left = (width - 283)/2;
        int top = (height - 180)/2;
        context.drawTexture(TEXTURE, left, top, 0, 0, 283, 180, 283, 180);
        for (Drawable widget : widgets) {
            widget.render(context, mouseX, mouseY, delta);
        }
        if (this.client == null) return;
        int j;
        Text leftText;
        Text rightText;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(this.client.player);
        switch (currentPage) {
            case 0:
                context.drawTexture(new Identifier(SoulForge.MOD_ID, "icon.png"), left + 32, top + 19, 0, 0, 79, 79, 79, 79);
                leftText = Text.translatable("encyclopedia.front_page.text");
                j = 0;
                for (OrderedText text : textRenderer.wrapLines(leftText, 105)) {
                    context.drawCenteredTextWithShadow(textRenderer, text, left + 71, top + 102 + j, 0xbbbbbb);
                    j += 9;
                }
                break;
            case 77:
                leftText = Text.translatable("encyclopedia.siphons");
                j = 0;
                for (OrderedText text : textRenderer.wrapLines(leftText, 105)) {
                    context.drawCenteredTextWithShadow(textRenderer, text, left + 71, top + 17 + j, 0xbbbbbb);
                    j += 9;
                }
                rightText = Text.translatable("encyclopedia.siphons.determination");
                j = 0;
                for (OrderedText text : textRenderer.wrapLines(rightText, 105)) {
                    context.drawCenteredTextWithShadow(textRenderer, text, left + 211, top + 17 + j, 0xbbbbbb);
                    j += 9;
                }
                break;
            case 78:
                leftText = Text.translatable("encyclopedia.siphons.bravery");
                j = 0;
                for (OrderedText text : textRenderer.wrapLines(leftText, 105)) {
                    context.drawCenteredTextWithShadow(textRenderer, text, left + 71, top + 17 + j, 0xbbbbbb);
                    j += 9;
                }
                rightText = Text.translatable("encyclopedia.siphons.justice");
                j = 0;
                for (OrderedText text : textRenderer.wrapLines(rightText, 105)) {
                    context.drawCenteredTextWithShadow(textRenderer, text, left + 211, top + 17 + j, 0xbbbbbb);
                    j += 9;
                }
                break;
            case 79:
                leftText = Text.translatable("encyclopedia.siphons.kindness");
                j = 0;
                for (OrderedText text : textRenderer.wrapLines(leftText, 105)) {
                    context.drawCenteredTextWithShadow(textRenderer, text, left + 71, top + 17 + j, 0xbbbbbb);
                    j += 9;
                }
                rightText = Text.translatable("encyclopedia.siphons.patience");
                j = 0;
                for (OrderedText text : textRenderer.wrapLines(rightText, 105)) {
                    context.drawCenteredTextWithShadow(textRenderer, text, left + 211, top + 17 + j, 0xbbbbbb);
                    j += 9;
                }
                break;
            case 80:
                leftText = Text.translatable("encyclopedia.siphons.integrity");
                j = 0;
                for (OrderedText text : textRenderer.wrapLines(leftText, 105)) {
                    context.drawCenteredTextWithShadow(textRenderer, text, left + 71, top + 17 + j, 0xbbbbbb);
                    j += 9;
                }
                rightText = Text.translatable("encyclopedia.siphons.perseverance");
                j = 0;
                for (OrderedText text : textRenderer.wrapLines(rightText, 105)) {
                    context.drawCenteredTextWithShadow(textRenderer, text, left + 211, top + 17 + j, 0xbbbbbb);
                    j += 9;
                }
                break;
        }
        int i = 0;
        for (TraitBase trait : Traits.all()) {
            i++;
            if (currentPage == i) {
                context.drawCenteredTextWithShadow(textRenderer, trait.getLocalizedText(), left + 71, top + 17, trait.getColor());
                MutableText text = Text.translatable("encyclopedia.trait." + trait.getName().toLowerCase() + ".text");
                j = 9;
                for (OrderedText line : textRenderer.wrapLines(text, 105)) {
                    context.drawCenteredTextWithShadow(textRenderer, line, left + 71, top + 17 + j, 0xbbbbbb);
                    j += 9;
                }
            } else {
                List<AbilityBase> abilities = List.copyOf(trait.getAbilities());
                abilities = abilities.stream().sorted(Comparator.comparingInt(AbilityBase::getLV)).toList();
                for (AbilityBase ability : abilities) {
                    i++;
                    if (currentPage == i) {
                        Identifier texture;
                        List<Text> data = new ArrayList<>();
                        MutableText description = Text.translatable("encyclopedia.ability." + ability.getID().getPath() + ".text");
                        if (playerSoul.hasDiscovered(ability)) {
                            texture = new Identifier(SoulForge.MOD_ID, "textures/ui/ability_icon/" + ability.getID().getPath() + ".png");
                            data.add(ability.getLocalizedText());
                            data.add(Text.literal("\"").append(ability.getTooltip()).append("\""));
                            data.add(Text.translatable("encyclopedia.prefixes.lv").append("" + ability.getLV()));
                            data.add(Text.translatable("encyclopedia.prefixes.cost").append("" + ability.getCost()));
                            int cooldown = ability.getCooldown();
                            MutableText cooldownText;
                            if (cooldown == 0) cooldownText = Text.translatable("encyclopedia.cooldown.none");
                            else if (cooldown < 20) cooldownText = Text.literal(cooldown + " ").append(Text.translatable("encyclopedia.cooldown.ticks"));
                            else if (cooldown < 1200) cooldownText = Text.literal(String.format("%.02f ", (float)cooldown/20f)).append(Text.translatable("encyclopedia.cooldown.seconds"));
                            else cooldownText = Text.literal(String.format("%.02f ", (float)cooldown/1200f)).append(Text.translatable("encyclopedia.cooldown.minutes"));
                            data.add(Text.translatable("encyclopedia.prefixes.cooldown").append(cooldownText));
                        } else {
                            texture = new Identifier(SoulForge.MOD_ID, "textures/ui/ability_icon/grayscale/" + ability.getID().getPath() + ".png");
                            data.add(Text.translatable("encyclopedia.unlock_text").append("" + ability.getLV()));
                            description = Text.translatable("encyclopedia.missing_ability.text");
                        }

                        String desc = description.getString();
                        while (desc.contains("${")) {
                            String toReplace = desc.substring(desc.indexOf("${") + 2, desc.indexOf("}"));
                            String calc = toReplace.replace("lv", String.valueOf(playerSoul.getLV()));
                            int value = 0;
                            try {
                                value = (int)StringCalculator.getResult(calc);
                            } catch (Exception e) {
                                SoulForge.LOGGER.warn("Exception occurred while attempting to calculate string " + calc + ". Exception: " + e);
                            }
                            desc = desc.replace("${" + toReplace + "}", String.valueOf(value));
                        }
                        description = Text.literal(desc);

                        context.drawTexture(texture, left + 33, top + 19, 0, 0, 79, 79, 79, 79);
                        j = 0;
                        for (Text line : data) {
                            for (OrderedText text : textRenderer.wrapLines(line, 105)) {
                                context.drawCenteredTextWithShadow(textRenderer, text, left + 71, top + 102 + j, 0xbbbbbb);
                                j += 9;
                            }
                        }
                        j = 0;
                        for (OrderedText text : textRenderer.wrapLines(description, 105)) {
                            context.drawCenteredTextWithShadow(textRenderer, text, left + 211, top + 17 + j, 0xbbbbbb);
                            j += 9;
                        }
                    }
                }
            }
        }
        i = 80;
        for (int k = 0; k < Traits.all().size()-1; k++) {
            TraitBase trait1 = Traits.all().get(k);
            if (trait1 == Traits.determination) continue;
            for (int l = k+1; l < Traits.all().size(); l++) {
                TraitBase trait2 = Traits.all().get(l);
                if (trait2 == Traits.determination) continue;
                if (trait1 == trait2) continue;
                i++;
                if (currentPage == i) {
                    List<Text> data = new ArrayList<>();
                    MutableText description = Text.translatable("encyclopedia.dual." + trait1.getName().toLowerCase() + "_" + trait2.getName().toLowerCase() + ".text");
                    if (!playerSoul.getResetData().hasDual(trait1, trait2)) {
                        String text = Text.translatable("encyclopedia.unlock_dual").getString();
                        data.add(Text.literal(text.replace("{0}", trait1.getName() + "-" + trait2.getName())));
                        description = Text.translatable("encyclopedia.missing_dual.text");
                    } else {
                        data.add(trait1.getLocalizedText().append("-").append(trait2.getLocalizedText()));
                    }

                    String desc = description.getString();
                    while (desc.contains("${")) {
                        String toReplace = desc.substring(desc.indexOf("${") + 2, desc.indexOf("}"));
                        String calc = toReplace.replace("lv", String.valueOf(playerSoul.getLV()));
                        int value = 0;
                        try {
                            value = (int)StringCalculator.getResult(calc);
                        } catch (Exception e) {
                            SoulForge.LOGGER.warn("Exception occurred while attempting to calculate string " + calc + ". Exception: " + e);
                        }
                        desc = desc.replace("${" + toReplace + "}", String.valueOf(value));
                    }
                    description = Text.literal(desc);

                    j = 0;
                    for (Text line : data) {
                        for (OrderedText text : textRenderer.wrapLines(line, 105)) {
                            context.drawCenteredTextWithShadow(textRenderer, text, left + 71, top + 17 + j, 0xbbbbbb);
                            j += 9;
                        }
                    }
                    j = 0;
                    for (OrderedText text : textRenderer.wrapLines(description, 105)) {
                        context.drawCenteredTextWithShadow(textRenderer, text, left + 211, top + 17 + j, 0xbbbbbb);
                        j += 9;
                    }
                }
            }
        }
        for (int k = 0; k < Traits.all().size()-1; k++) {
            TraitBase trait = Traits.all().get(k);
            if (trait == Traits.determination) continue;
            i++;
            if (currentPage == i) {
                List<Text> data = new ArrayList<>();
                MutableText description = Text.translatable("encyclopedia.pure." + trait.getName().toLowerCase() + ".text");
                if (!playerSoul.getResetData().hasPure(trait)) {
                    String text = Text.translatable("encyclopedia.unlock_pure").getString();
                    data.add(Text.literal(text.replace("{0}", trait.getName())));
                    description = Text.translatable("encyclopedia.missing_pure.text");
                } else {
                    data.add(trait.getLocalizedText());
                }

                String desc = description.getString();
                while (desc.contains("${")) {
                    String toReplace = desc.substring(desc.indexOf("${") + 2, desc.indexOf("}"));
                    String calc = toReplace.replace("lv", String.valueOf(playerSoul.getLV()));
                    int value = 0;
                    try {
                        value = (int)StringCalculator.getResult(calc);
                    } catch (Exception e) {
                        SoulForge.LOGGER.warn("Exception occurred while attempting to calculate string " + calc + ". Exception: " + e);
                    }
                    desc = desc.replace("${" + toReplace + "}", String.valueOf(value));
                }
                description = Text.literal(desc);

                j = 0;
                for (Text line : data) {
                    for (OrderedText text : textRenderer.wrapLines(line, 105)) {
                        context.drawCenteredTextWithShadow(textRenderer, text, left + 71, top + 17 + j, 0xbbbbbb);
                        j += 9;
                    }
                }
                j = 0;
                for (OrderedText text : textRenderer.wrapLines(description, 105)) {
                    context.drawCenteredTextWithShadow(textRenderer, text, left + 211, top + 17 + j, 0xbbbbbb);
                    j += 9;
                }
            }
        }
    }

    public static class PageButton extends ClickableWidget {
        private final boolean isLeft;
        private final PressAction pressAction;

        protected PageButton(int x, int y, boolean isLeft, PressAction pressAction) {
            super(x, y, 23, 13, Text.empty());
            this.isLeft = isLeft;
            this.pressAction = pressAction;
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            int u = isHovered() ? 26 : 3;
            int v = isLeft ? 207 : 194;

            context.drawTexture(new Identifier("textures/gui/book.png"), this.getX(), this.getY(), u, v, 18, 10, 256, 256);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (isHovered()) {
                this.pressAction.onPress();
            }
        }

        public interface PressAction {
            void onPress();
        }
    }

    public static class ContentButton extends PressableWidget {
        private final PressAction pressAction;

        protected ContentButton(int x, int y, int width, int height, PressAction pressAction, String text) {
            super(x, y, width, height, Text.literal(text));
            this.pressAction = pressAction;
        }

        protected ContentButton(int x, int y, int width, int height, PressAction pressAction, Text text) {
            super(x, y, width, height, text);
            this.pressAction = pressAction;
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }

        @Override
        public void onPress() {
            this.pressAction.onPress();
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            this.drawMessage(context, MinecraftClient.getInstance().textRenderer, 0xbbbbbb);
        }

        public interface PressAction {
            void onPress();
        }
    }

    /*public PageState getPageState(int page) {
        return pageState;
    }

    public PageState getPageState(int page) {
        PageState output = null;
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(SoulForge.MOD_ID);
        if (modContainer.isPresent()) {
            Optional<Path> path = modContainer.get().findPath("./data/" + SoulForge.MOD_ID + "/encyclopedia_pages/page_" + page + ".json");
            if (path.isPresent()) {
                try {
                    JsonReader reader = new JsonReader(new FileReader(path.get().toFile()));
                    Gson gson = new Gson();
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    if (json.has("trait")) {
                        output = gson.fromJson(reader, TraitPageState.class);
                    } else if (json.has("ability")) {
                        output = gson.fromJson(reader, AbilityPageState.class);
                    } else {
                        output = gson.fromJson(reader, PageState.class);
                    }
                } catch (Exception e) {
                    SoulForge.LOGGER.warn("Attempted to read invalid file! File path: " + path.get().toString() + ". Error: " + e);
                }
            }
        }
        return output;
    }

    public static class PageState {
        private String leftText;
        private String rightText;
        private String leftImage;
        private String rightImage;
    }

    public static class TraitPageState extends PageState {
        private String trait;
    }

    public static class AbilityPageState extends PageState {
        private String ability;
    }*/
}
