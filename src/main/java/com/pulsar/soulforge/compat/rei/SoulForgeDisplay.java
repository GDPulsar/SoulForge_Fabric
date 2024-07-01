package com.pulsar.soulforge.compat.rei;

import com.pulsar.soulforge.recipe.SoulForgeRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoulForgeDisplay extends BasicDisplay {
    public SoulForgeDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public SoulForgeDisplay(SoulForgeRecipe recipe) {
        super(getInputList(recipe), List.of(EntryIngredient.of(EntryStacks.of(recipe.getResult(null)))));
    }

    private static List<EntryIngredient> getInputList(SoulForgeRecipe recipe) {
        if (recipe == null) return Collections.emptyList();
        List<EntryIngredient> list = new ArrayList<>();
        if (recipe.getInput().isPresent()) list.add(EntryIngredients.ofIngredient(recipe.getInput().get()));
        else list.add(EntryIngredient.empty());
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            list.add(EntryIngredients.ofIngredient(recipe.getIngredients().get(i)));
        }
        return list;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SoulForgeCategory.SOUL_FORGE;
    }
}
