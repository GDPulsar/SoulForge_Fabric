package com.pulsar.soulforge.advancement;

import com.google.gson.JsonObject;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.Abilities;
import com.pulsar.soulforge.ability.AbilityBase;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.Optional;

public class CastAbilityCriterion extends AbstractCriterion<CastAbilityCriterion.Conditions> {
    @Override
    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate predicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        String ability = obj.get("ability").getAsString();
        return new Conditions(ability, getId(), predicate);
    }

    @Override
    public Identifier getId() {
        return new Identifier(SoulForge.MOD_ID, "cast_ability");
    }

    public static class Conditions extends AbstractCriterionConditions {
        AbilityBase ability;

        public Conditions(String abilityName, Identifier id, LootContextPredicate playerPredicate) {
            super(id, playerPredicate);
            this.ability = Abilities.get(abilityName);
        }
        boolean requirementsMet(AbilityBase ability) {
            return Objects.equals(ability.getName(), this.ability.getName());
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            JsonObject json = super.toJson(serializer);
            json.addProperty("ability", ability.getName());
            return json;
        }
    }

    public void trigger(ServerPlayerEntity player, AbilityBase ability) {
        trigger(player, conditions -> conditions.requirementsMet(ability));
    }
}
