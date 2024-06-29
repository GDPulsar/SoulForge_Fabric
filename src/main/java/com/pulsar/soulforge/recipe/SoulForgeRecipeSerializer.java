package com.pulsar.soulforge.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pulsar.soulforge.SoulForge;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.dynamic.Codecs;

import java.util.ArrayList;
import java.util.List;

public class SoulForgeRecipeSerializer implements RecipeSerializer<SoulForgeRecipe> {
    private SoulForgeRecipeSerializer() {}

    public static final SoulForgeRecipeSerializer INSTANCE = new SoulForgeRecipeSerializer();
    public static final Identifier ID = new Identifier(SoulForge.MOD_ID, "soul_forge_recipe");

    @Override
    public SoulForgeRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient input = null;
        if (buf.readBoolean()) input = Ingredient.fromPacket(buf);
        int ingredientCount = buf.readVarInt();
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientCount; i++) {
            ingredients.add(Ingredient.fromPacket(buf));
        }
        ItemStack result = buf.readItemStack();
        return new SoulForgeRecipe(id, input, ingredients, result);
    }

    @Override
    public SoulForgeRecipe read(Identifier id, JsonObject json) {
        Ingredient input = null;
        if (json.has("input")) input = Ingredient.fromJson(json.get("input"));
        List<Ingredient> ingredients = new ArrayList<>();
        JsonArray ingredientArray = json.getAsJsonArray("ingredients");
        for (int i = 0; i < ingredientArray.size(); i++) {
            ingredients.add(Ingredient.fromJson(ingredientArray.get(i)));
        }
        ItemStack result = ShapedRecipe.outputFromJson(json.get("result").getAsJsonObject());
        return new SoulForgeRecipe(id, input, ingredients, result);
    }

    @Override
    public void write(PacketByteBuf buf, SoulForgeRecipe recipe) {
        buf.writeBoolean(recipe.getInput().isPresent());
        if (recipe.getInput().isPresent()) recipe.getInput().get().write(buf);
        buf.writeVarInt(recipe.getIngredients().size());
        for (Ingredient ingredient : recipe.getIngredients()) ingredient.write(buf);
        buf.writeItemStack(recipe.getOutput(null));
    }
}
