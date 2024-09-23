package com.pulsar.soulforge.compat.rei;

import com.pulsar.soulforge.block.SoulForgeBlocks;
import com.pulsar.soulforge.client.ui.SoulForgeScreen;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.recipe.SiphonRecipe;
import com.pulsar.soulforge.recipe.SoulForgeRecipe;
import com.pulsar.soulforge.recipe.SoulForgeRecipes;
import com.pulsar.soulforge.tag.SoulForgeTags;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SoulForgeREIClientPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new SoulForgeCategory());
        registry.add(new SiphonCategory());

        registry.addWorkstations(SoulForgeCategory.SOUL_FORGE, EntryStacks.of(SoulForgeBlocks.SOUL_FORGE_BLOCK));
        registry.addWorkstations(SiphonCategory.SIPHONING, EntryStacks.of(Blocks.SMITHING_TABLE));
        registry.addWorkstations(SiphonCategory.SIPHONING, EntryStacks.of(SoulForgeItems.SIPHON_TEMPLATE));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(SoulForgeRecipe.class, SoulForgeRecipe.Type.INSTANCE, SoulForgeDisplay::new);

        registry.registerDisplayGenerator(SiphonCategory.SIPHONING, new DynamicDisplayGenerator<SiphonDisplay>() {
            @Override
            public Optional<List<SiphonDisplay>> getRecipeFor(EntryStack<?> entry) {
                return getMatchesOf(entry);
            }

            @Override
            public Optional<List<SiphonDisplay>> getUsageFor(EntryStack<?> entry) {
                return getMatchesOf(entry);
            }

            @Override
            public Optional<List<SiphonDisplay>> generate(ViewSearchBuilder builder) {
                return getMatchesOf(builder.getRecipesFor());
            }

            public Optional<List<SiphonDisplay>> getMatchesOf(@Nullable EntryStack<?> entry) {
                List<SiphonDisplay> displays = new ArrayList<>();
                List<SiphonRecipe> siphonRecipes = registry.getRecipeManager().listAllOfType(SoulForgeRecipes.SIPHON_RECIPE);
                for (SiphonRecipe recipe : siphonRecipes) {
                    if (entry != null) {
                        if (entry.getValue() instanceof ItemStack stack) {
                            if (stack.isIn(SoulForgeTags.SIPHONABLE) || stack.isIn(SoulForgeTags.ARTIFACT_SIPHONABLE)) {
                                EntryIngredient template = EntryIngredients.ofIngredient(recipe.getTemplate());
                                EntryIngredient addition = EntryIngredients.ofIngredient(recipe.getAddition());
                                EntryIngredient output = EntryIngredients.of(recipe.getOutputFromInput(stack));
                                displays.add(new SiphonDisplay(List.of(template, EntryIngredients.of(stack), addition), List.of(output)));
                            }
                        }
                    } else {
                        for (ItemStack validBase : recipe.getBase().getMatchingStacks()) {
                            EntryIngredient template = EntryIngredients.ofIngredient(recipe.getTemplate());
                            EntryIngredient addition = EntryIngredients.ofIngredient(recipe.getAddition());
                            EntryIngredient output = EntryIngredients.of(recipe.getOutputFromInput(validBase));
                            displays.add(new SiphonDisplay(List.of(template, EntryIngredients.of(validBase), addition), List.of(output)));
                        }
                    }
                }
                return Optional.of(displays);
            }

            public Optional<List<SiphonDisplay>> getMatchesOf(@Nullable List<EntryStack<?>> entries) {
                List<SiphonDisplay> displays = new ArrayList<>();
                List<SiphonRecipe> siphonRecipes = registry.getRecipeManager().listAllOfType(SoulForgeRecipes.SIPHON_RECIPE);
                for (SiphonRecipe recipe : siphonRecipes) {
                    if (entries != null) {
                        for (EntryStack<?> entry : entries) {
                            if (entry.getValue() instanceof ItemStack stack) {
                                if (stack.isIn(SoulForgeTags.SIPHONABLE) || stack.isIn(SoulForgeTags.ARTIFACT_SIPHONABLE)) {
                                    EntryIngredient template = EntryIngredients.ofIngredient(recipe.getTemplate());
                                    EntryIngredient addition = EntryIngredients.ofIngredient(recipe.getAddition());
                                    EntryIngredient output = EntryIngredients.of(recipe.getOutputFromInput(stack));
                                    displays.add(new SiphonDisplay(List.of(template, EntryIngredients.of(stack), addition), List.of(output)));
                                }
                            }
                        }
                    } else {
                        for (ItemStack validBase : recipe.getBase().getMatchingStacks()) {
                            EntryIngredient template = EntryIngredients.ofIngredient(recipe.getTemplate());
                            EntryIngredient addition = EntryIngredients.ofIngredient(recipe.getAddition());
                            EntryIngredient output = EntryIngredients.of(recipe.getOutputFromInput(validBase));
                            displays.add(new SiphonDisplay(List.of(template, EntryIngredients.of(validBase), addition), List.of(output)));
                        }
                    }
                }
                if (displays.isEmpty()) return Optional.empty();
                return Optional.of(displays);
            }
        });
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(93, 36, 13, 13), SoulForgeScreen.class, SoulForgeCategory.SOUL_FORGE);
    }
}
