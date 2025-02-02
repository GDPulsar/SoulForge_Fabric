package com.pulsar.soulforge.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ImmobilizedEffect extends StatusEffect {
    protected ImmobilizedEffect() {
        super(StatusEffectCategory.HARMFUL, 0x00FF00);
    }
}
