package com.pulsar.soulforge.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.Codecs;

import java.util.Objects;
import java.util.Optional;

public class MonsterSoulCriterion extends AbstractCriterion<MonsterSoulCriterion.Conditions> {
    public static final Codec<Conditions> CODEC = RecordCodecBuilder.<Conditions>create(conditions -> conditions.group(
            Codecs.POSITIVE_INT.fieldOf("count").forGetter(Conditions::getCount),
            Codecs.NON_EMPTY_STRING.fieldOf("type").forGetter(Conditions::getType),
            LootContextPredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::player)
    ).apply(conditions, Conditions::new));

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return CODEC;
    }

    public static class Conditions implements AbstractCriterion.Conditions {
        int count;
        String type;
        Optional<LootContextPredicate> predicate;

        public int getCount() { return count; }
        public String getType() { return type; }

        public Conditions(int count, String type, Optional<LootContextPredicate> predicate) {
            this.count = count;
            this.type = type;
            this.predicate = predicate;
        }
        boolean requirementsMet(int count, String type) {
            return Objects.equals(type, this.type) && count == this.count;
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return predicate;
        }
    }

    public void trigger(ServerPlayerEntity player, int count, String type) {
        trigger(player, conditions -> conditions.requirementsMet(count, type));
    }
}
