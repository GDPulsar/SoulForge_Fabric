package com.pulsar.soulforge.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pulsar.soulforge.SoulForge;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class SoulForgeRecipeSerializer implements RecipeSerializer<SoulForgeRecipe> {
    private SoulForgeRecipeSerializer() {}

    public static final SoulForgeRecipeSerializer INSTANCE = new SoulForgeRecipeSerializer();
    public static final Identifier ID = Identifier.of(SoulForge.MOD_ID, "soul_forge_recipe");

    public static MapCodec<SoulForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.ALLOW_EMPTY_CODEC.fieldOf("input").forGetter(SoulForgeRecipe::getInnerIngredient),
            Codec.list(Ingredient.ALLOW_EMPTY_CODEC).fieldOf("ingredients").forGetter(SoulForgeRecipe::getOuterIngredients),
            ItemStack.CODEC.fieldOf("result").forGetter(SoulForgeRecipe::getResultItem)
    ).apply(instance, SoulForgeRecipe::new));

    public static PacketCodec<RegistryByteBuf, SoulForgeRecipe> PACKET_CODEC = PacketCodec.tuple(
            Ingredient.PACKET_CODEC, SoulForgeRecipe::getInnerIngredient,
            PacketCodecs.collection((ArrayList::new), Ingredient.PACKET_CODEC), SoulForgeRecipe::getOuterIngredients,
            ItemStack.PACKET_CODEC, SoulForgeRecipe::getResultItem,
            SoulForgeRecipe::new
    );
    @Override
    public MapCodec<SoulForgeRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, SoulForgeRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
