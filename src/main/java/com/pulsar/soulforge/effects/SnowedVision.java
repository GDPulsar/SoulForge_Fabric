package com.pulsar.soulforge.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class SnowedVision extends StatusEffect {
    protected SnowedVision() {
        super(StatusEffectCategory.HARMFUL, 0xFFFFFF);
    }
}
