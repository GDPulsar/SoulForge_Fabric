package com.pulsar.soulforge.ability;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class DataDrivenAuraAbility extends AuraAbilityBase {
    public Identifier id;
    public String name;
    public int unlockLv;
    public int cost;
    public int cooldown;
    public HashMap<EntityAttribute, EntityAttributeModifier> modifiers;

    @Override
    public HashMap<EntityAttribute, EntityAttributeModifier> getModifiers(int elv) {
        HashMap<EntityAttribute, EntityAttributeModifier> scaledModifiers = new HashMap<>();
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> modifier : modifiers.entrySet()) {
            EntityAttributeModifier scaledModifier = new EntityAttributeModifier(modifier.getValue().getId(), modifier.getValue().getName(),
                    modifier.getValue().getValue() * elv, modifier.getValue().getOperation());
            scaledModifiers.put(modifier.getKey(), scaledModifier);
        }
        return scaledModifiers;
    }

    public DataDrivenAuraAbility(Identifier id, String name, int unlockLv, int cost, int cooldown, HashMap<EntityAttribute, EntityAttributeModifier> modifiers) {
        this.id = id;
        this.name = name;
        this.unlockLv = unlockLv;
        this.cost = cost;
        this.cooldown = cooldown;
        this.modifiers = modifiers;
    }

    public Identifier getID() {
        return this.id;
    }

    @Override
    public int getLV() {
        return this.unlockLv;
    }

    @Override
    public int getCost() {
        return this.cost;
    }

    @Override
    public int getCooldown() {
        return this.cooldown;
    }

    @Override
    public AbilityBase getInstance() {
        return new DataDrivenAuraAbility(this.id, this.name, this.unlockLv, this.cost, this.cooldown, this.modifiers);
    }
}
