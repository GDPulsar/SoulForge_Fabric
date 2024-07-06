package com.pulsar.soulforge.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ManaSickness extends StatusEffect {
    public ManaSickness() {
        super(
                StatusEffectCategory.HARMFUL,
                0x000000
        );
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
