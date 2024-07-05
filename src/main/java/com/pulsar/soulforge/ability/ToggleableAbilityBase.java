package com.pulsar.soulforge.ability;

public abstract class ToggleableAbilityBase extends AbilityBase {
    @Override
    public AbilityType getType() {
        return AbilityType.TOGGLE;
    }
}
