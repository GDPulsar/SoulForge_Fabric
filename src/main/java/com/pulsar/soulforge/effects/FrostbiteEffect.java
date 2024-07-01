package com.pulsar.soulforge.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class FrostbiteEffect extends StatusEffect {
    protected FrostbiteEffect() {
        super(StatusEffectCategory.HARMFUL, 0x7777FF);
    }

    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.damage(entity.getDamageSources().generic(), 1f * (amplifier + 1));
        return true;
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 40 == 0;
    }
}
