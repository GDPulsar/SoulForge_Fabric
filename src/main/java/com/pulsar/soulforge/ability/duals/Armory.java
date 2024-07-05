package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;

public class Armory extends AbilityBase {
    public int getLV() { return 1; }

    public int getCost() { return 20; }

    public int getCooldown() { return 0; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Armory();
    }
}
