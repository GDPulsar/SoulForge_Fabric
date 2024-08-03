package com.pulsar.soulforge.ability.despair;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;

public class DrainingField extends ToggleableAbilityBase {
    @Override
    public int getLV() {
        return 20;
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
    public AbilityBase getInstance() {
        return new DrainingField();
    }
}
