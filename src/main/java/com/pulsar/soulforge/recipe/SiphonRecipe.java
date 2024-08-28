package com.pulsar.soulforge.recipe;

import com.google.gson.JsonObject;
import com.pulsar.soulforge.SoulForge;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SiphonRecipe implements SmithingRecipe {
    final Identifier id;
    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;
    final String siphonStr;

    SiphonRecipe(Identifier id, Ingredient template, Ingredient base, Ingredient addition, String siphonStr) {
        this.id = id;
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.siphonStr = siphonStr;
    }

    public boolean matches(Inventory inventory, World world) {
        return this.template.test(inventory.getStack(0)) && this.base.test(inventory.getStack(1)) && this.addition.test(inventory.getStack(2));
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        ItemStack armor = inventory.getStack(1).copy();
        if (armor.getNbt() != null) {
            armor.getNbt().putString("Siphon", siphonStr);
            return armor;
        }
        return null;
    }

    public Ingredient getTemplate() { return template; }
    public Ingredient getBase() { return base; }
    public Ingredient getAddition() { return addition; }

    public ItemStack getOutputFromInput(ItemStack input) {
        ItemStack itemStack = input.copy();
        itemStack.getOrCreateNbt().putString("Siphon", siphonStr);
        return itemStack;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);
        itemStack.getOrCreateNbt().putString("Siphon", siphonStr);
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

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public Identifier getId() {
        return id;
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
        public static Identifier ID = new Identifier(SoulForge.MOD_ID, "siphon_recipe");

        public Serializer() {}

        @Override
        public SiphonRecipe read(Identifier id, PacketByteBuf buf) {
            Ingredient ingredient = Ingredient.fromPacket(buf);
            Ingredient ingredient2 = Ingredient.fromPacket(buf);
            Ingredient ingredient3 = Ingredient.fromPacket(buf);
            String siphonStr = buf.readString();
            return new SiphonRecipe(id, ingredient, ingredient2, ingredient3, siphonStr);
        }

        @Override
        public SiphonRecipe read(Identifier id, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(json.get("template"));
            Ingredient ingredient1 = Ingredient.fromJson(json.get("base"));
            Ingredient ingredient2 = Ingredient.fromJson(json.get("addition"));
            String siphonStr = json.get("siphonStr").getAsString();
            return new SiphonRecipe(id, ingredient, ingredient1, ingredient2, siphonStr);
        }

        public void write(PacketByteBuf packetByteBuf, SiphonRecipe recipe) {
            recipe.template.write(packetByteBuf);
            recipe.base.write(packetByteBuf);
            recipe.addition.write(packetByteBuf);
            packetByteBuf.writeString(recipe.siphonStr);
        }
    }
}
