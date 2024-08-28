package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ScreenAbilityBase;
import com.pulsar.soulforge.client.networking.OpenScreenPacket;

public class WeaponWheel extends ScreenAbilityBase {
    @Override
    public OpenScreenPacket.ScreenType screenType() {
        return OpenScreenPacket.ScreenType.WEAPON_WHEEL;
    }

    public int getLV() { return 20; }
    public int getCost() { return 20; }
    public int getCooldown() { return 0; }
    public AbilityType getType() { return AbilityType.WEAPON; }
    @Override
    public AbilityBase getInstance() {
        return new WeaponWheel();
    }
}
