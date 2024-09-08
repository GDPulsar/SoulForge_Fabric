package com.pulsar.soulforge.ability;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.UUID;

public class DataDrivenAbilitySerializer {
    public static AbilityBase fromJson(JsonObject json) throws JsonParseException {
        String name = json.get("name").getAsString();
        Identifier id = Identifier.tryParse(json.get("id").getAsString());
        int unlockLv = json.get("unlockLv").getAsInt();
        int cost = json.get("cost").getAsInt();
        int cooldown = json.get("cooldown").getAsInt();
        switch (json.get("type").getAsString()) {
            case "cast" -> {
                return new DataDrivenAbility(id, name, unlockLv, cost, cooldown, AbilityType.CAST);
            }
            case "toggle" -> {
                return new DataDrivenToggleableAbility(id, name, unlockLv, cost, cooldown);
            }
            case "aura" -> {
                JsonArray modifierArray = json.get("modifiers").getAsJsonArray();
                HashMap<EntityAttribute, EntityAttributeModifier> modifiers = new HashMap<>();
                for (int i = 0; i < modifierArray.size(); i++) {
                    JsonObject modifierJson = modifierArray.get(i).getAsJsonObject();
                    EntityAttribute attribute = Registries.ATTRIBUTE.get(Identifier.tryParse(modifierJson.get("attribute").getAsString()));
                    UUID modifierId = UUID.fromString(modifierJson.get("uuid").getAsString());
                    String modifierName = modifierJson.get("name").getAsString();
                    double modifierValue = modifierJson.get("value").getAsDouble();
                    int modifierOperation = modifierJson.get("operation").getAsInt();
                    EntityAttributeModifier modifier = new EntityAttributeModifier(modifierId, modifierName, modifierValue, EntityAttributeModifier.Operation.fromId(modifierOperation));
                    modifiers.put(attribute, modifier);
                }
                return new DataDrivenAuraAbility(id, name, unlockLv, cost, cooldown, modifiers);
            }
            case "weapon" -> {
                Item weapon = Registries.ITEM.get(Identifier.tryParse(json.get("item").getAsString()));
                return new DataDrivenWeaponAbility(id, name, unlockLv, cost, cooldown, weapon);
            }
        }
        return new DataDrivenAbility(id, name, unlockLv, cost, cooldown, AbilityType.CAST);
    }
}
