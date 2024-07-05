package com.pulsar.soulforge.ability;

import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Constants;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;

public class Abilities {
    public static AbilityBase get(Identifier id) {
        for (TraitBase trait : Traits.all()) {
            for (AbilityBase ability : List.copyOf(trait.getAbilities())) {
                if (ability.getID().equals(id)) {
                    return ability;
                }
            }
        }
        return null;
    }

    public static AbilityBase get(String name) {
        for (TraitBase trait : Traits.all()) {
            for (AbilityBase ability : List.copyOf(trait.getAbilities())) {
                if (Objects.equals(ability.getName(), name)) {
                    return ability.getInstance();
                }
            }
        }
        for (TraitBase trait1 : Traits.all()) {
            if (trait1 != Traits.determination) {
                for (TraitBase trait2 : Traits.all()) {
                    if (trait2 != Traits.determination && trait2 != trait1) {
                        for (AbilityBase ability : Constants.getDualTraitAbilities(List.of(trait1, trait2))) {
                            if (Objects.equals(ability.getName(), name)) {
                                return ability.getInstance();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
