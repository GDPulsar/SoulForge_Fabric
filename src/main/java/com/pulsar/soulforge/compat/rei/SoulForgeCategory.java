package com.pulsar.soulforge.compat.rei;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.block.SoulForgeBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class SoulForgeCategory implements DisplayCategory<BasicDisplay> {
    public static final Identifier TEXTURE = Identifier.of(SoulForge.MOD_ID, "textures/ui/soul_forge_rei.png");
    public static final CategoryIdentifier<SoulForgeDisplay> SOUL_FORGE = CategoryIdentifier.of(SoulForge.MOD_ID, "soul_forge");

    @Override
    public CategoryIdentifier<? extends BasicDisplay> getCategoryIdentifier() {
        return SOUL_FORGE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("soulforge.rei_category.title");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(SoulForgeBlocks.SOUL_FORGE_BLOCK.asItem().getDefaultStack());
    }

    @Override
    public List<Widget> setupDisplay(BasicDisplay display, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - 88, bounds.getCenterY() - 42);
        List<Widget> widgets = new LinkedList<>();
        widgets.add(Widgets.createTexturedWidget(TEXTURE, new Rectangle(startPoint.x-1, startPoint.y-1, 176, 85)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 46, startPoint.y + 34)).entries(display.getInputEntries().get(0)));
        if (display.getInputEntries().size() >= 2) widgets.add(Widgets.createSlot(new Point(startPoint.x + 25, startPoint.y + 34)).entries(display.getInputEntries().get(1)));
        if (display.getInputEntries().size() >= 3) widgets.add(Widgets.createSlot(new Point(startPoint.x + 36, startPoint.y + 13)).entries(display.getInputEntries().get(2)));
        if (display.getInputEntries().size() >= 4) widgets.add(Widgets.createSlot(new Point(startPoint.x + 57, startPoint.y + 13)).entries(display.getInputEntries().get(3)));
        if (display.getInputEntries().size() >= 5) widgets.add(Widgets.createSlot(new Point(startPoint.x + 66, startPoint.y + 34)).entries(display.getInputEntries().get(4)));
        if (display.getInputEntries().size() >= 6) widgets.add(Widgets.createSlot(new Point(startPoint.x + 36, startPoint.y + 55)).entries(display.getInputEntries().get(5)));
        if (display.getInputEntries().size() >= 7) widgets.add(Widgets.createSlot(new Point(startPoint.x + 57, startPoint.y + 55)).entries(display.getInputEntries().get(6)));
        Slot output = Widgets.createSlot(new Point(startPoint.x + 119, startPoint.y + 34));
        output.setBackgroundEnabled(false);
        widgets.add(output.markOutput().entries(display.getOutputEntries().get(0)));

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 85;
    }
}
