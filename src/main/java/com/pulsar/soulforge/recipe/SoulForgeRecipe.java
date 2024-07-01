package com.pulsar.soulforge.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SoulForgeRecipe implements Recipe<SoulForgeRecipe.Input> {
    @Nullable
    private final Ingredient input;
    private final List<Ingredient> ingredients;
    private final ItemStack result;

    public SoulForgeRecipe(Ingredient input, List<Ingredient> ingredients, ItemStack result) {
        this.input = input;
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

    public Ingredient getInnerIngredient() { return this.input; }
    public List<Ingredient> getOuterIngredients() { return this.ingredients; }
    public ItemStack getResultItem() { return this.result; }

    @Override
    public boolean matches(Input input, World world) {
        if (input.getSize() != 7 || filledSlots(input) != ingredients.size()) return false;
        for (int i = 1; i <= ingredients.size(); i++) {
            if (!has(input, ingredients.get(i-1))) return false;
        }
        return this.input == null || this.input.test(input.getStackInSlot(0));
    }

    @Override
    public ItemStack craft(Input input, RegistryWrapper.WrapperLookup lookup) {
        for (int i = 0; i < input.getSize(); i++) {
            input.getStackInSlot(i).decrement(1);
        }
        return this.result.copy();
    }

    public int filledSlots(Input input) {
        int count = 0;
        for (int i = 1; i < 7; i++) {
            if (!input.getStackInSlot(i).isEmpty()) count++;
        }
        return count;
    }

    public boolean has(Input input, Ingredient ingredient) {
        for (int i = 1; i < 7; i++) {
            if (ingredient.test(input.getStackInSlot(i))) return true;
        }
        return false;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.result;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
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

    public static class Input implements RecipeInput {
        Inventory inventory;

        public Input(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return inventory.getStack(slot);
        }

        @Override
        public int getSize() {
            return inventory.size();
        }
    }
}
