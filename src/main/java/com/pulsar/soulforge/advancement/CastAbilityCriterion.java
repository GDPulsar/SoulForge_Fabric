package com.pulsar.soulforge.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pulsar.soulforge.ability.AbilityBase;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class CastAbilityCriterion extends AbstractCriterion<CastAbilityCriterion.Conditions> {
    public static final Codec<Conditions> CODEC = RecordCodecBuilder.<Conditions>create(conditions -> conditions.group(
            Identifier.CODEC.fieldOf("ability").forGetter(Conditions::getAbility),
            LootContextPredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::getPredicate)
    ).apply(conditions, Conditions::new));

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return CODEC;
    }

    @Override
    public AdvancementCriterion<Conditions> create(Conditions conditions) {
        return super.create(conditions);
    }

    public static class Conditions implements AbstractCriterion.Conditions {
        Identifier abilityId;
        Optional<LootContextPredicate> predicate;

        public Identifier getAbility() {
            return abilityId;
        }

        public Optional<LootContextPredicate> getPredicate() {
            return predicate;
        }

        public Conditions(Identifier abilityId, Optional<LootContextPredicate> predicate) {
            this.abilityId = abilityId;
            this.predicate = predicate;
        }

        boolean requirementsMet(AbilityBase ability) {
            return ability.getID() == this.getAbility();
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return predicate;
        }
    }

    public void trigger(ServerPlayerEntity player, AbilityBase ability) {
        trigger(player, conditions -> conditions.requirementsMet(ability));
    }
}
