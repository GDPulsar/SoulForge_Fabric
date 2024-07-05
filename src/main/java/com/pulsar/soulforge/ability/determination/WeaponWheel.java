package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;

public class WeaponWheel extends AbilityBase {
    public int getLV() { return 20; }

    public int getCost() { return 20; }

    public int getCooldown() { return 0; }

    public AbilityType getType() { return AbilityType.WEAPON; }

    @Override
    public AbilityBase getInstance() {
        return new WeaponWheel();
    }
}
