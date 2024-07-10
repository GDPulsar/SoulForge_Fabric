package com.pulsar.soulforge.data;

import com.pulsar.soulforge.ability.AbilityBase;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class AbilityList {
    public HashMap<String, AbilityBase> abilities = new HashMap<>();

    public AbilityBase get(String name) {
        return abilities.getOrDefault(name, null);
    }

    public AbilityBase get(AbilityBase ability) {
        return abilities.getOrDefault(ability.getName(), null);
    }

    @Nullable
    public <T extends AbilityBase> T getTyped(T instance) {
        return (T)abilities.getOrDefault(abilities.get(instance.getName()), null);
    }

    public List<AbilityBase> getAll() {
        return List.copyOf(abilities.values());
    }

    public List<AbilityBase> getActive() {
        return List.copyOf(abilities.values().stream().filter(AbilityBase::getActive).toList());
    }

    public List<AbilityBase> getOnCooldown() {
        return List.copyOf(abilities.values().stream().filter(AbilityBase::onCooldown).toList());
    }

    public int getCooldown(String name) {
        if (get(name) == null) return 0;
        return get(name).getCooldownVal();
    }

    public int getCooldown(AbilityBase ability) {
        if (get(ability) == null) return 0;
        return get(ability).getCooldownVal();
    }

    public void add(AbilityBase ability) {
        abilities.put(ability.getName(), ability);
    }

    public void set(List<AbilityBase> abilities) {
        HashMap<String, AbilityBase> newAbilities = new HashMap<>();
        for (AbilityBase ability : abilities) {
            newAbilities.put(ability.getName(), ability);
        }
        this.abilities = newAbilities;
    }

    public boolean has(AbilityBase ability) {
        return abilities.containsKey(ability.getName());
    }

    public boolean has(String name) {
        return abilities.containsKey(name);
    }

    public void remove(String name) {
        abilities.remove(name);
    }

    public void remove(AbilityBase ability) {
        abilities.remove(ability.getName());
    }

    public boolean isActive(String name) {
        try { return get(name).getActive(); }
        catch (NullPointerException exception) { return false; }
    }

    public boolean isActive(AbilityBase ability) {
        try { return get(ability).getActive(); }
        catch (NullPointerException exception) { return false; }
    }
}
