package com.pulsar.soulforge.data;

import com.pulsar.soulforge.ability.AbilityBase;
import net.minecraft.util.math.MathHelper;
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
        return abilities.values().stream().toList();
    }

    public List<AbilityBase> getActive() {
        return abilities.values().stream().filter(AbilityBase::getActive).toList();
    }

    public int getCooldown(String name, int currentTick) {
        return MathHelper.clamp(get(name).getCooldown() - (currentTick - get(name).getLastCastTime()), 0, get(name).getCooldown());
    }

    public int getCooldown(AbilityBase ability, int currentTick) {
        return MathHelper.clamp(get(ability).getCooldown() - (currentTick - get(ability).getLastCastTime()), 0, get(ability).getCooldown());
    }

    public void add(AbilityBase ability) {
        abilities.put(ability.getName(), ability);
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
