package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ScreenAbilityBase;
import com.pulsar.soulforge.client.networking.OpenScreenPacket;

public class Rampage extends ScreenAbilityBase {
    @Override
    public OpenScreenPacket.ScreenType screenType() {
        return OpenScreenPacket.ScreenType.RAMPAGE;
    }

    @Override
    public int getLV() { return 20; }
    @Override
    public int getCost() { return 100; }
    @Override
    public int getCooldown() { return 4800; }
    @Override
    public AbilityBase getInstance() { return new Rampage(); }
}
