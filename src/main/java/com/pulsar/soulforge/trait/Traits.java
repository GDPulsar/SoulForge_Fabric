package com.pulsar.soulforge.trait;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.Abilities;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.determination.DeterminationSword;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.trait.traits.*;
import com.pulsar.soulforge.util.Constants;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
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
        List<TraitBase> traits = new ArrayList<>(Arrays.asList(bravery, justice, kindness, patience, integrity, perseverance, determination));
        traits.addAll(customTraits.values());
        return traits;
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

    public static List<AbilityBase> getAbilities(PlayerEntity player) {
        if (player == null) return List.of();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        return getAbilities(player, playerSoul);
    }

    public static List<AbilityBase> getAbilities(PlayerEntity player, SoulComponent playerSoul) {
        return getAbilities(playerSoul.getTraits(), playerSoul.getLV(), playerSoul.isPure(), Utils.hasHate(player));
    }

    public static List<AbilityBase> getAbilities(List<TraitBase> traits, int lv, boolean pure, boolean hasHate) {
        List<AbilityBase> abilities = new ArrayList<>();
        for (TraitBase trait : traits) {
            for (AbilityBase ability : trait.getAbilities()) {
                if (!Constants.isAllowedForDualTrait(ability, traits, lv)) continue;
                if (ability.getLV() <= lv) {
                    if (ability instanceof DeterminationSword && lv >= 20) continue;
                    abilities.add(ability.getInstance());
                }
            }
        }
        for (AbilityBase ability : Constants.getDualTraitAbilities(traits)) {
            if (ability.getLV() <= lv) {
                abilities.add(ability.getInstance());
            }
        }

        if (pure && !traits.contains(Traits.spite)) {
            for (TraitBase trait : traits) {
                if (trait != Traits.perseverance && trait != Traits.despair && trait != Traits.determination) {
                    AbilityBase pureAbility = Constants.pureAbilities.get(trait);
                    if (pureAbility.getLV() <= lv) {
                        abilities.add(pureAbility.getInstance());
                    }
                }
            }
        }
        if (traits.contains(Traits.spite)) {
            for (TraitBase trait : Traits.all()) {
                if (trait != Traits.perseverance && trait != Traits.despair && trait != Traits.determination) {
                    AbilityBase pureAbility = Constants.pureAbilities.get(trait);
                    if (pureAbility.getLV() <= lv) {
                        abilities.add(pureAbility.getInstance());
                    }
                }
            }
        }

        if (hasHate) {
            for (AbilityBase ability : Abilities.hateAbilities) {
                abilities.add(ability.getInstance());
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
                        if (ability.getLV() <= soul.getLV() || soul.hasTrait(Traits.spite)) {
                            if (ability.getType() != AbilityType.PASSIVE && ability.getType() != AbilityType.PASSIVE_ON_HIT) {
                                if (ability instanceof DeterminationSword && soul.getLV() == 20) continue;
                                abilityNames.add(ability.getName());
                            }
                        }
                    }
                    if (soul.isPure() || soul.hasTrait(Traits.spite) || Objects.equals(mode, "Determination")) {
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
                        if (ability.getType() == AbilityType.PASSIVE || ability.getType() == AbilityType.PASSIVE_ON_HIT) {
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
