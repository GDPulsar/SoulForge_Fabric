package com.pulsar.soulforge.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class UnchainedEffect extends StatusEffect {
    public UnchainedEffect() {
        super(
                StatusEffectCategory.BENEFICIAL,
                0xFF4444
        );
    }
}
