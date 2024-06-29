package com.pulsar.soulforge.effects;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class CrushedEffect extends StatusEffect {
    public CrushedEffect() {
        super(
                StatusEffectCategory.HARMFUL,
                0x2e0a61
        );
        this.addAttributeModifier(EntityAttributes.GENERIC_ARMOR, "fbfb68a5-3642-4fc9-94fa-0ed4806ddb96", -15, EntityAttributeModifier.Operation.ADDITION);
    }
}
