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

import java.util.Optional;

public class PlayerLVCriterion extends AbstractCriterion<PlayerLVCriterion.Conditions> {
    @Override
    protected Conditions conditionsFromJson(JsonObject obj, LootContextPredicate predicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        int minimumLv = obj.get("min").getAsInt();
        Conditions conditions = new Conditions(minimumLv, getId(), predicate);
        return conditions;
    }

    @Override
    public Identifier getId() {
        return new Identifier(SoulForge.MOD_ID, "player_lv");
    }

    public static class Conditions extends AbstractCriterionConditions {
        int minimumLv;

        public Conditions(int minimumLv, Identifier id, LootContextPredicate predicate) {
            super(id, predicate);
            this.minimumLv = minimumLv;
        }
        boolean requirementsMet(int lv) {
            return lv >= minimumLv;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer serializer) {
            JsonObject json = super.toJson(serializer);
            json.addProperty("min", minimumLv);
            return json;
        }
    }

    public void trigger(ServerPlayerEntity player, int lv) {
        trigger(player, conditions -> conditions.requirementsMet(lv));
    }
}
