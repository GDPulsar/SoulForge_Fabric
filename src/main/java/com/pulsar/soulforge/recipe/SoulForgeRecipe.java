package com.pulsar.soulforge.recipe;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SoulForgeRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    @Nullable
    private final Ingredient input;
    private final List<Ingredient> ingredients;
    private final ItemStack result;

    public SoulForgeRecipe(Identifier id, @Nullable Ingredient input, List<Ingredient> ingredients, ItemStack result) {
        this.id = id;
        this.input = input;
        this.ingredients = ingredients;
        this.result = result;
    }

    public SoulForgeRecipe(Identifier id, Optional<Ingredient> input, List<Ingredient> ingredients, ItemStack result) {
        this.id = id;
        this.input = input.orElse(null);
        this.ingredients = ingredients;
        this.result = result;
    }

    public Optional<Ingredient> getInput() { return Optional.ofNullable(this.input); }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.ofSize(this.ingredients.size());
        list.addAll(this.ingredients);
        return list;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (inventory.size() != 7 || filledSlots(inventory) != ingredients.size()) return false;
        for (int i = 1; i <= ingredients.size(); i++) {
            if (!has(inventory, ingredients.get(i-1))) return false;
        }
        if (input == null) return true;
        return input.test(inventory.getStack(0));
    }

    public int filledSlots(SimpleInventory inventory) {
        int count = 0;
        for (int i = 1; i < 7; i++) {
            if (!inventory.getStack(i).isEmpty()) count++;
        }
        return count;
    }

    public boolean has(SimpleInventory inventory, Ingredient ingredient) {
        for (int i = 1; i < 7; i++) {
            if (ingredient.test(inventory.getStack(i))) return true;
        }
        return false;
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        for (ItemStack input : inventory.stacks) {
            input.decrement(1);
        }
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.result;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SoulForgeRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<SoulForgeRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "soul_forge_recipe";
    }
}
