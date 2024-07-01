package com.pulsar.soulforge.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.trait.TraitBase;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class PlayerTraitCriterion extends AbstractCriterion<PlayerTraitCriterion.Conditions> {
    public static final Codec<Conditions> CODEC = RecordCodecBuilder.<Conditions>create(conditions -> conditions.group(
            Identifier.CODEC.optionalFieldOf("trait1", null).forGetter(Conditions::getTrait1),
            Identifier.CODEC.optionalFieldOf("trait2", null).forGetter(Conditions::getTrait2),
            Codec.INT.optionalFieldOf("count", -1).forGetter(Conditions::getCount),
            Codec.BOOL.optionalFieldOf("strong", false).forGetter(Conditions::getStrong),
            Codec.BOOL.optionalFieldOf("pure", false).forGetter(Conditions::getPure),
            LootContextPredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::player)
    ).apply(conditions, Conditions::new));

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return CODEC;
    }

    public static class Conditions implements AbstractCriterion.Conditions {
        Identifier trait1;
        Identifier trait2;
        int count;
        boolean strong;
        boolean pure;
        Optional<LootContextPredicate> predicate;

        public Identifier getTrait1() { return trait1; }
        public Identifier getTrait2() { return trait2; }
        public int getCount() { return count; }
        public boolean getStrong() { return strong; }
        public boolean getPure() { return pure; }

        public Conditions(Identifier trait1, Identifier trait2, int count, boolean strong, boolean pure, Optional<LootContextPredicate> predicate) {
            this.trait1 = trait1;
            this.trait2 = trait2;
            this.count = count;
            this.strong = strong;
            this.pure = pure;
            this.predicate = predicate;
        }

        boolean requirementsMet(SoulComponent playerSoul) {
            List<TraitBase> traits = playerSoul.getTraits();
            if (strong && !(playerSoul.isStrong() || playerSoul.isPure())) return false;
            if (pure && !playerSoul.isPure()) return false;
            if (trait1 != null) {
                if (trait2 == null) return traits.contains(trait1);
                return traits.contains(trait1) && traits.contains(trait2);
            } else if (count != -1) {
                return traits.size() >= count;
            }
            return false;
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return predicate;
        }
    }

    public void trigger(ServerPlayerEntity player, SoulComponent playerSoul) {
        trigger(player, conditions -> conditions.requirementsMet(playerSoul));
    }
}
