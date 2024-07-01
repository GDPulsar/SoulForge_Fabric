package com.pulsar.soulforge.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

public class PlayerLVCriterion extends AbstractCriterion<PlayerLVCriterion.Conditions> {
    public static final Codec<Conditions> CODEC = RecordCodecBuilder.<Conditions>create(conditions -> conditions.group(
            Codecs.POSITIVE_INT.fieldOf("min").forGetter(Conditions::getMin),
            LootContextPredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::player)
    ).apply(conditions, Conditions::new));

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return CODEC;
    }

    public static class Conditions implements AbstractCriterion.Conditions {
        int minimumLv;
        Optional<LootContextPredicate> predicate;

        public int getMin() { return minimumLv; }

        public Conditions(int minimumLv, Optional<LootContextPredicate> predicate) {
            this.minimumLv = minimumLv;
            this.predicate = predicate;
        }
        boolean requirementsMet(int lv) {
            return lv >= minimumLv;
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return predicate;
        }
    }

    public void trigger(ServerPlayerEntity player, int lv) {
        trigger(player, conditions -> conditions.requirementsMet(lv));
    }
}
