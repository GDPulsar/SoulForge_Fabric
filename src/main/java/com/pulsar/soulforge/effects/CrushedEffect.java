package com.pulsar.soulforge.effects;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;

public class CrushedEffect extends StatusEffect {
    public CrushedEffect() {
        super(
                StatusEffectCategory.HARMFUL,
                0x2e0a61
        );
        this.addAttributeModifier(EntityAttributes.GENERIC_ARMOR, Identifier.of(SoulForge.MOD_ID, "crushed"), -15, EntityAttributeModifier.Operation.ADD_VALUE);
    }
}
