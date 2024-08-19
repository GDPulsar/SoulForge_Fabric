package com.pulsar.soulforge.trait;

import com.google.gson.*;
import com.pulsar.soulforge.ability.Abilities;
import com.pulsar.soulforge.ability.AbilityBase;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataDrivenTrait implements TraitBase {
    public String name;
    public String translationKey;
    public List<AbilityBase> abilities;
    public Style traitStyle;
    public int color;

    public DataDrivenTrait(String name, String translationKey, List<Identifier> abilities, Style traitStyle, int color) {
        this.name = name;
        this.translationKey = translationKey;
        this.abilities = new ArrayList<>();
        for (Identifier id : abilities) {
            this.abilities.add(Abilities.get(id));
        }
        this.traitStyle = traitStyle;
        this.color = color;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MutableText getLocalizedText() {
        return Text.translatable(translationKey).setStyle(getStyle());
    }

    @Override
    public List<AbilityBase> getAbilities() {
        return abilities;
    }

    @Override
    public Style getStyle() {
        return Optional.ofNullable(traitStyle).orElse(Style.EMPTY.withColor(getColor()));
    }

    @Override
    public int getColor() {
        return color;
    }

    public static class DataDrivenTraitSerializer implements JsonSerializer<DataDrivenTrait>, JsonDeserializer<DataDrivenTrait> {
        @Override
        public JsonElement serialize(DataDrivenTrait trait, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("name", trait.name);
            json.addProperty("translationKey", trait.translationKey);
            JsonArray abilitiesJson = new JsonArray();
            for (AbilityBase ability : trait.abilities) {
                abilitiesJson.add(ability.getID().toString());
            }
            json.add("abilities", abilitiesJson);
            if (trait.traitStyle != null) {
                json.add("style", new Style.Serializer().serialize(trait.traitStyle, typeOfSrc, context));
            }
            JsonObject colorJson = new JsonObject();
            Color color = new Color(trait.color);
            colorJson.addProperty("r", color.getRed());
            colorJson.addProperty("g", color.getGreen());
            colorJson.addProperty("b", color.getBlue());
            json.add("color", colorJson);
            return null;
        }

        @Override
        public DataDrivenTrait deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = element.getAsJsonObject();
            String name = json.get("name").getAsString();
            String translationKey = json.get("translationKey").getAsString();
            JsonArray abilitiesJson = json.get("abilities").getAsJsonArray();
            List<Identifier> abilities = new ArrayList<>();
            for (int i = 0; i < abilitiesJson.size(); i++) {
                abilities.add(new Identifier(abilitiesJson.get(i).getAsString()));
            }
            JsonObject colorJson = json.get("color").getAsJsonObject();
            Style style = json.has("style") ? new Style.Serializer().deserialize(json.get("style"), typeOfT, context) : null;
            Color color = new Color(colorJson.get("r").getAsInt(), colorJson.get("g").getAsInt(), colorJson.get("b").getAsInt());
            return new DataDrivenTrait(name, translationKey, abilities, style, color.getRGB());
        }
    }
}
