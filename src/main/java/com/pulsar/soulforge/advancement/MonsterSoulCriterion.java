package com.pulsar.soulforge.advancement;

import com.google.gson.JsonObject;
import com.pulsar.soulforge.SoulForge;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.Optional;

public class MonsterSoulCriterion extends AbstractCriterion<MonsterSoulCriterion.Conditions> {
    @Override
    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate predicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        int count = obj.get("count").getAsInt();
        String type = obj.get("type").getAsString();
        Conditions conditions = new Conditions(count, type, getId(), predicate);
        return conditions;
    }

    @Override
    public Identifier getId() {
        return new Identifier(SoulForge.MOD_ID, "monster_soul");
    }

    public static class Conditions extends AbstractCriterionConditions {
        int count;
        String type;

        public Conditions(int count, String type, Identifier id, LootContextPredicate predicate) {
            super(id, predicate);
            this.count = count;
            this.type = type;
        }
        boolean requirementsMet(int count, String type) {
            return Objects.equals(type, this.type) && count == this.count;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            JsonObject json = super.toJson(serializer);
            json.addProperty("count", count);
            json.addProperty("type", type);
            return json;
        }
    }

    public void trigger(ServerPlayerEntity player, int count, String type) {
        trigger(player, conditions -> conditions.requirementsMet(count, type));
    }
}
