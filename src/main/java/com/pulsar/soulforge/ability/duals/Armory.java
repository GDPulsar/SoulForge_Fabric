package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ScreenAbilityBase;
import com.pulsar.soulforge.client.networking.OpenScreenPacket;

public class Armory extends ScreenAbilityBase {
    @Override
    public OpenScreenPacket.ScreenType screenType() {
        return OpenScreenPacket.ScreenType.ARMORY;
    }

    public int getLV() { return 1; }
    public int getCost() { return 20; }
    public int getCooldown() { return 0; }
    @Override
    public AbilityBase getInstance() {
        return new Armory();
    }
}
