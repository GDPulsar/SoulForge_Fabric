package com.pulsar.soulforge.trait;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.determination.DeterminationSword;
import com.pulsar.soulforge.ability.patience.Iceshock;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.trait.traits.*;
import com.pulsar.soulforge.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Traits {
    public static TraitBase bravery = new Bravery();
    public static TraitBase justice = new Justice();
    public static TraitBase kindness = new Kindness();
    public static TraitBase patience = new Patience();
    public static TraitBase integrity = new Integrity();
    public static TraitBase perseverance = new Perseverance();
    public static TraitBase determination = new Determination();
    public static TraitBase fear = new Fear();
    public static TraitBase ineptitude = new Ineptitude();
    public static TraitBase misery = new Misery();
    public static TraitBase anxiety = new Anxiety();
    public static TraitBase paranoia = new Paranoia();
    public static TraitBase despair = new Despair();
    public static TraitBase spite = new Spite();

    private static HashMap<String, TraitBase> customTraits = new HashMap<>();

    public static TraitBase randomNormal() {
        Random rnd = new Random();
        return switch (rnd.nextInt(6)) {
            case 0 -> bravery;
            case 1 -> integrity;
            case 2 -> justice;
            case 3 -> kindness;
            case 4 -> patience;
            case 5 -> perseverance;
            default ->
                // this shouldn't be reachable, but just in case.
                    bravery;
        };
    }

    public static void addCustomTrait(TraitBase trait) {
        customTraits.put(trait.getName(), trait);
    }

    public static List<TraitBase> all() {
        return new ArrayList<>(Arrays.asList(bravery, justice, kindness, patience, integrity, perseverance, determination));
    }

    public static List<TraitBase> trueAll() {
        List<TraitBase> traits = new ArrayList<>(Arrays.asList(
                bravery, justice, kindness, patience,
                integrity, perseverance, determination,
                fear, ineptitude, misery, anxiety,
                paranoia, despair, spite));
        traits.addAll(customTraits.values());
        return traits;
    }

    @Nullable
    public static TraitBase get(String traitName) {
        for (TraitBase trait : Traits.trueAll()) {
            if (Objects.equals(trait.getName(), traitName)) {
                return trait;
            }
        }
        return null;
    }

    public static List<AbilityBase> getAbilities(List<TraitBase> traits, int lv, boolean isPure) {
        List<AbilityBase> abilities = new ArrayList<>();
        for (TraitBase trait : traits) {
            for (AbilityBase ability : trait.getAbilities()) {
                if (!Constants.isAllowedForDualTrait(ability, traits, lv)) continue;
                if (ability.getLV() <= lv) {
                    if (ability instanceof DeterminationSword && lv >= 20) continue;
                    if (ability instanceof Iceshock && lv >= 10) continue;
                    abilities.add(ability.getInstance());
                }
            }
        }
        for (AbilityBase ability : Constants.getDualTraitAbilities(traits)) {
            if (ability.getLV() <= lv) {
                abilities.add(ability.getInstance());
            }
        }

        if (isPure && traits.get(0) != Traits.spite) {
            for (TraitBase trait : traits) {
                if (trait != Traits.perseverance && trait != Traits.determination) {
                    AbilityBase pureAbility = Constants.pureAbilities.get(trait);
                    if (pureAbility.getLV() <= lv) {
                        abilities.add(pureAbility.getInstance());
                    }
                }
            }
        }
        if (traits.contains(Traits.spite)) {
            for (TraitBase trait : Traits.all()) {
                if (trait != Traits.perseverance && trait != Traits.determination) {
                    AbilityBase pureAbility = Constants.pureAbilities.get(trait);
                    if (pureAbility.getLV() <= lv) {
                        abilities.add(pureAbility.getInstance());
                    }
                }
            }
        }
        abilities.sort(Comparator.comparingInt(AbilityBase::getLV));
        return abilities;
    }

    public static List<AbilityBase> getModeAbilities(String mode, SoulComponent soul) {
        List<String> abilityNames = new ArrayList<>();
        if (!Objects.equals(mode, "Passives") && !Objects.equals(mode, "Duals")) {
            for (TraitBase trait : Traits.trueAll()) {
                if (Objects.equals(trait.getName(), mode)) {
                    for (AbilityBase ability : trait.getAbilities()) {
                        if (ability.getLV() <= soul.getLV() || soul.getTraits().contains(Traits.spite)) {
                            if (ability.getType() != AbilityType.PASSIVE && ability.getType() != AbilityType.PASSIVE_NOCAST) {
                                if (ability instanceof DeterminationSword && soul.getLV() == 20) continue;
                                if (ability instanceof Iceshock && soul.getLV() >= 10) continue;
                                abilityNames.add(ability.getName());
                            }
                        }
                    }
                    if (soul.isPure() || soul.getTraits().contains(Traits.spite) || Objects.equals(mode, "Determination")) {
                        if (trait == Traits.perseverance || trait == Traits.despair || trait == Traits.determination) continue;
                        AbilityBase pureAbility = Constants.pureAbilities.get(trait);
                        if (pureAbility.getLV() <= soul.getLV()) {
                            abilityNames.add(pureAbility.getName());
                        }
                    }
                }
            }
        } else if (Objects.equals(mode, "Duals")) {
            for (AbilityBase ability : Constants.getDualTraitAbilities(soul.getTraits())) {
                if (ability.getLV() <= soul.getLV()) {
                    abilityNames.add(ability.getName());
                }
            }
        } else {
            for (TraitBase trait : soul.getTraits()) {
                for (AbilityBase ability : trait.getAbilities()) {
                    if (ability.getLV() <= soul.getLV()) {
                        if (ability.getType() == AbilityType.PASSIVE || ability.getType() == AbilityType.PASSIVE_NOCAST) {
                            abilityNames.add(ability.getName());
                        }
                    }
                }
            }
        }
        List<AbilityBase> abilities = new ArrayList<>();
        for (String abilityName : abilityNames) {
            try {
                AbilityBase ability = soul.getAbility(abilityName);
                if (ability != null) abilities.add(ability);
            } catch (Exception ignored) {}
        }
        abilities.sort(Comparator.comparingInt(AbilityBase::getLV));
        return abilities;
    }
}
