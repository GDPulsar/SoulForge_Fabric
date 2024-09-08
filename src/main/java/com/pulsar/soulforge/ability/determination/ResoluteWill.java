package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;

public class ResoluteWill extends AbilityBase {
    public int getLV() { return 1; }

    public int getCost() { return 0; }

    public int getCooldown() { return 0; }

    public AbilityType getType() { return AbilityType.PASSIVE; }

    @Override
    public AbilityBase getInstance() {
        return new ResoluteWill();
    }
}
