package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;

public class Wormhole extends AbilityBase {
    public int getLV() { return 15; }

    public int getCost() { return 30; }

    public int getCooldown() { return 600; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Wormhole();
    }
}
