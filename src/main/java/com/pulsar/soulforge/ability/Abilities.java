package com.pulsar.soulforge.ability;

import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
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
                    return ability;
                }
            }
        }
        return null;
    }
}
