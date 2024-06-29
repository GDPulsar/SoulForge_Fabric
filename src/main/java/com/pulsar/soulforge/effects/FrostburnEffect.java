package com.pulsar.soulforge.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class FrostburnEffect extends StatusEffect {
    protected FrostburnEffect() {
        super(StatusEffectCategory.HARMFUL, 0x9977FF);
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.damage(entity.getDamageSources().generic(), amplifier/2f + 1f);
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 10 == 0;
    }
}
