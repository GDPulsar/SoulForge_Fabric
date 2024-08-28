package com.pulsar.soulforge.ability;

public abstract class SideEffectAbilityBase extends AbilityBase {
    public abstract float getOccurrenceChance();

    @Override
    public int getLV() {
        return 0;
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public AbilityType getType() {
        return AbilityType.SIDE_EFFECT;
    }
}
