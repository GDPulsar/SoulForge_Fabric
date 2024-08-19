package com.pulsar.soulforge.ability;

import com.pulsar.soulforge.ability.hate.sideeffects.Insanity;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Constants;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
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
        // work
        for (TraitBase trait : Traits.trueAll()) {
            if (trait != Traits.spite) {
                for (AbilityBase ability : List.copyOf(trait.getAbilities())) {
                    if (Objects.equals(ability.getName(), name)) {
                        return ability.getInstance();
                    }
                }
            }
            if (Constants.pureAbilities.containsKey(trait)) {
                if (Objects.equals(Constants.pureAbilities.get(trait).getName(), name)) {
                    return Constants.pureAbilities.get(trait).getInstance();
                }
            }
        }
        for (AbilityBase ability : Constants.dualAbilities) {
            if (Objects.equals(ability.getName(), name)) {
                return ability.getInstance();
            }
        }
        return null;
    }

    public static List<AbilityBase> hateAbilities = new ArrayList<>(List.<AbilityBase>of(
            new Insanity()
    ));
}
