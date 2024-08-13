package com.pulsar.soulforge.compat.rei;

import com.pulsar.soulforge.recipe.SiphonRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SiphonDisplay extends BasicDisplay {
    public SiphonDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    public SiphonDisplay(SiphonRecipe recipe) {
        super(getInputList(recipe), List.of(EntryIngredient.of(EntryStacks.of(recipe.getOutput(null)))));
    }

    private static List<EntryIngredient> getInputList(SiphonRecipe recipe) {
        if (recipe == null) return Collections.emptyList();
        List<EntryIngredient> list = new ArrayList<>();
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            list.add(EntryIngredients.ofIngredient(recipe.getIngredients().get(i)));
        }
        return list;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return SiphonCategory.SIPHONING;
    }
}
