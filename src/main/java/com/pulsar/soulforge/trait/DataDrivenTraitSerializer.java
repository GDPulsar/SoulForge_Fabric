package com.pulsar.soulforge.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DataDrivenTraitSerializer {
    public static DataDrivenTrait fromJson(JsonObject json) throws JsonParseException {
        String name = json.get("name").getAsString();
        Identifier id = Identifier.tryParse(json.get("id").getAsString());
        JsonArray abilitiesJson = json.get("abilities").getAsJsonArray();
        List<Identifier> abilities = new ArrayList<>();
        for (int i = 0; i < abilitiesJson.size(); i++) {
            abilities.add(new Identifier(abilitiesJson.get(i).getAsString()));
        }
        Identifier pureAbility = null;
        if (json.has("pureAbility")) pureAbility = Identifier.tryParse(json.get("pureAbility").getAsString());
        JsonObject colorJson = json.get("color").getAsJsonObject();
        Style style = json.has("style") ? new Style.Serializer().deserialize(json.get("style"), null, null) : null;
        Color color = new Color(colorJson.get("r").getAsInt(), colorJson.get("g").getAsInt(), colorJson.get("b").getAsInt());
        return new DataDrivenTrait(id, name, abilities, pureAbility, style, color.getRGB());
    }
}
