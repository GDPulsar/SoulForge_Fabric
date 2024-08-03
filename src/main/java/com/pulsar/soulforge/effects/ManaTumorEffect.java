package com.pulsar.soulforge.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ManaTumorEffect extends StatusEffect {
    protected ManaTumorEffect() {
        super(StatusEffectCategory.HARMFUL, 0x006000);
    }
}
