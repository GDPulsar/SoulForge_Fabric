package com.pulsar.soulforge.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ValiantHeart extends StatusEffect {
    public ValiantHeart() {
        super(
                StatusEffectCategory.BENEFICIAL,
                0xfcca03
        );
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
