package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;

public class Reload extends AbilityBase {
    public int getLV() { return 12; }
    public int getCost() { return 35; }
    public int getCooldown() { return 300; }
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Reload();
    }
}
