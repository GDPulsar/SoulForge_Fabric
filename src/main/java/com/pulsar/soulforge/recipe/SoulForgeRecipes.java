package com.pulsar.soulforge.recipe;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SoulForgeRecipes {
    public static RecipeType<SoulForgeRecipe> SOUL_FORGE_RECIPE;
    public static RecipeType<SiphonRecipe> SIPHON_RECIPE;

    public static void register() {
        Registry.register(Registries.RECIPE_SERIALIZER, SoulForgeRecipeSerializer.ID, SoulForgeRecipeSerializer.INSTANCE);
        SOUL_FORGE_RECIPE = Registry.register(Registries.RECIPE_TYPE, Identifier.of(SoulForge.MOD_ID, SoulForgeRecipe.Type.ID), SoulForgeRecipe.Type.INSTANCE);
        Registry.register(Registries.RECIPE_SERIALIZER, SiphonRecipe.Serializer.ID, SiphonRecipe.Serializer.INSTANCE);
        SIPHON_RECIPE = Registry.register(Registries.RECIPE_TYPE, Identifier.of(SoulForge.MOD_ID, SiphonRecipe.Type.ID), SiphonRecipe.Type.INSTANCE);
    }
}
