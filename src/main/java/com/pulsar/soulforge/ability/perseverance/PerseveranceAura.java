package com.pulsar.soulforge.ability.perseverance;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AuraAbilityBase;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class PerseveranceAura extends AuraAbilityBase {
    @Override
    public HashMap<EntityAttribute, EntityAttributeModifier> getModifiers(int elv) {
        return new HashMap<>(Map.ofEntries(
                entry(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier("perseverance_aura", elv / 2f, EntityAttributeModifier.Operation.ADDITION)),
                entry(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier("perseverance_aura", elv * 0.0175f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)),
                entry(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier("perseverance_aura", elv * 0.01f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL))
        ));
    }

    public int getLV() { return 15; }

    public int getCost() { return 100; }

    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new PerseveranceAura();
    }
}
