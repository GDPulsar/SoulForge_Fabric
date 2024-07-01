package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;

public class TrueLOVE extends AbilityBase {
    public String getName() { return "True LOVE"; }
    public int getLV() { return 1; }
    public int getCost() { return 0; }
    public int getCooldown() { return 0; }
    public AbilityType getType() { return AbilityType.PASSIVE_NOCAST; }

    @Override
    public AbilityBase getInstance() {
        return new TrueLOVE();
    }
}
