package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;

public class AntigravityZone extends ToggleableAbilityBase {
    public int getLV() { return 17; }
    public int getCost() { return 40; }
    public int getCooldown() { return 400; }

    @Override
    public AbilityBase getInstance() {
        return new AntigravityZone();
    }
}
