package com.pulsar.soulforge.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.siphon.Siphon;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SiphonRecipe implements SmithingRecipe {
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;
    final Siphon.Type siphonType;

    public SiphonRecipe(Ingredient template, Ingredient base, Ingredient addition, String siphonStr) {
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.siphonType = Siphon.Type.getSiphon(siphonStr);
    }

    public boolean matches(SmithingRecipeInput input, World world) {
        return this.template.test(input.getStackInSlot(0)) && this.base.test(input.getStackInSlot(1)) && this.addition.test(input.getStackInSlot(2));
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack armor = input.getStackInSlot(1).copy();
        armor.set(SoulForgeItems.SIPHON_COMPONENT, siphonType);
        return armor;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup lookup) {
        ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);
        itemStack.set(SoulForgeItems.SIPHON_COMPONENT, siphonType);
        return itemStack;
    }

    @Override
    public boolean testTemplate(ItemStack stack) {
        return this.template.test(stack);
    }

    @Override
    public boolean testBase(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return this.addition.test(stack);
    }

    public Ingredient getTemplate() { return this.template; }
    public Ingredient getBase() { return this.base; }
    public Ingredient getAddition() { return this.addition; }
    public String getSiphonStr() { return this.siphonType.asString(); }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<SiphonRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "siphon_recipe";
    }

    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<SiphonRecipe> {
        public static Serializer INSTANCE = new Serializer();
        public static Identifier ID = Identifier.of(SoulForge.MOD_ID, "siphon_recipe");

        public static MapCodec<SiphonRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("template").forGetter(SiphonRecipe::getTemplate),
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("base").forGetter(SiphonRecipe::getBase),
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(SiphonRecipe::getAddition),
                Codec.STRING.fieldOf("siphonStr").forGetter(SiphonRecipe::getSiphonStr)
        ).apply(instance, SiphonRecipe::new));

        public static PacketCodec<RegistryByteBuf, SiphonRecipe> PACKET_CODEC = PacketCodec.tuple(
                Ingredient.PACKET_CODEC, SiphonRecipe::getTemplate,
                Ingredient.PACKET_CODEC, SiphonRecipe::getBase,
                Ingredient.PACKET_CODEC, SiphonRecipe::getAddition,
                PacketCodecs.STRING, SiphonRecipe::getSiphonStr,
                SiphonRecipe::new
        );

        public Serializer() {}

        @Override
        public MapCodec<SiphonRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SiphonRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
