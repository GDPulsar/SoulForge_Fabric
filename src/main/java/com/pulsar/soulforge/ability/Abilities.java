package com.pulsar.soulforge.ability;

import com.pulsar.soulforge.ability.hate.sideeffects.Insanity;
import com.pulsar.soulforge.ability.other.BadToTheBone;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Constants;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Abilities {
    public static List<AbilityBase> specialAbilities = List.of(
            new BadToTheBone()
    );

    public static AbilityBase get(Identifier id) {
        for (TraitBase trait : Traits.trueAll()) {
            if (trait != Traits.spite) {
                for (AbilityBase ability : List.copyOf(trait.getAbilities())) {
                    if (ability.getID().equals(id)) {
                        return ability.getInstance();
                    }
                }
            }
            if (Constants.pureAbilities.containsKey(trait)) {
                if (Constants.pureAbilities.get(trait).getID().equals(id)) {
                    return Constants.pureAbilities.get(trait).getInstance();
                }
            }
        }
        for (AbilityBase ability : Constants.dualAbilities) {
            if (ability.getID().equals(id)) {
                return ability.getInstance();
            }
        }
        for (AbilityBase ability : hateAbilities) {
            if (ability.getID().equals(id)) {
                return ability.getInstance();
            }
        }
        return null;
    }

    public static AbilityBase get(String name) {
        if (name.contains(":")) return Abilities.get(Identifier.tryParse(name));
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
        for (AbilityBase ability : hateAbilities) {
            if (Objects.equals(ability.getName(), name)) {
                return ability.getInstance();
            }
        }
        for (AbilityBase ability : specialAbilities) {
            if (Objects.equals(ability.getName(), name)) {
                return ability.getInstance();
            }
        }
        return null;
    }

    public static List<AbilityBase> hateAbilities = new ArrayList<>(List.<AbilityBase>of(
            new Insanity()
    ));

    public static List<String> getAllAbilityNames() {
        List<String> abilityNames = new ArrayList<>();
        for (TraitBase trait : Traits.trueAll()) {
            if (trait != Traits.spite) {
                for (AbilityBase ability : List.copyOf(trait.getAbilities())) {
                    if (!abilityNames.contains(ability.getName()))
                        abilityNames.add(ability.getName());
                }
            }
            if (Constants.pureAbilities.containsKey(trait)) {
                if (!abilityNames.contains(Constants.pureAbilities.get(trait).getName()))
                    abilityNames.add(Constants.pureAbilities.get(trait).getName());
            }
        }
        for (AbilityBase ability : Constants.dualAbilities) {
            if (!abilityNames.contains(ability.getName()))
                abilityNames.add(ability.getName());
        }
        for (AbilityBase ability : hateAbilities) {
            if (!abilityNames.contains(ability.getName()))
                abilityNames.add(ability.getName());
        }
        return abilityNames;
    }

    public static int totalAbilityCount() {
        return getAllAbilityNames().size();
    }
}
