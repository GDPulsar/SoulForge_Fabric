package com.pulsar.soulforge.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class TumorImmunityEffect extends StatusEffect {
    protected TumorImmunityEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x44FF44);
    }
}
