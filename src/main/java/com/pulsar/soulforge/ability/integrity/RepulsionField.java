package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AuraAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class RepulsionField extends AuraAbilityBase {
    @Override
    public HashMap<EntityAttribute, EntityAttributeModifier> getModifiers(int elv) {
        return new HashMap<>(Map.ofEntries(
                entry(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier("repulsion_field", elv * 0.02f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)),
                entry(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS, new EntityAttributeModifier("repulsion_field", elv * 0.02f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)),
                entry(SoulForgeAttributes.GRAVITY_MODIFIER, new EntityAttributeModifier("repulsion_field", 0.5f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)),
                entry(SoulForgeAttributes.STEP_HEIGHT, new EntityAttributeModifier("repulsion_field", 1f, EntityAttributeModifier.Operation.ADDITION)),
                entry(SoulForgeAttributes.JUMP_MULTIPLIER, new EntityAttributeModifier("repulsion_field", 1f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL))
        ));
    }

    @Override
    public boolean hasFallImmunity() {
        return true;
    }

    public int getLV() { return 7; }

    public int getCost() { return 40; }

    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new RepulsionField();
    }
}
