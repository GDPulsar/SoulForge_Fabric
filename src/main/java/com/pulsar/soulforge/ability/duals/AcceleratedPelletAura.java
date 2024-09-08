package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AuraAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class AcceleratedPelletAura extends AuraAbilityBase {
    @Override
    public HashMap<EntityAttribute, EntityAttributeModifier> getModifiers(int elv) {
        return new HashMap<>(Map.ofEntries(
                entry(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier("accelerated_pellet_aura", elv * 0.0266f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)),
                entry(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS, new EntityAttributeModifier("accelerated_pellet_aura", elv * 0.0266f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)),
                entry(SoulForgeAttributes.GRAVITY_MODIFIER, new EntityAttributeModifier("accelerated_pellet_aura", 0.5f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)),
                entry(SoulForgeAttributes.STEP_HEIGHT, new EntityAttributeModifier("accelerated_pellet_aura", 1f, EntityAttributeModifier.Operation.ADDITION)),
                entry(SoulForgeAttributes.JUMP_MULTIPLIER, new EntityAttributeModifier("accelerated_pellet_aura", 1f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL))
        ));
    }

    @Override
    public boolean hasFallImmunity() {
        return true;
    }

    public int getLV() { return 15; }

    public int getCost() { return 100; }

    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new AcceleratedPelletAura();
    }
}
