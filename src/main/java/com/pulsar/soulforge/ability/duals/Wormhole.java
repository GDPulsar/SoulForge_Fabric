package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ScreenAbilityBase;
import com.pulsar.soulforge.client.networking.OpenScreenPacket;

public class Wormhole extends ScreenAbilityBase {
    @Override
    public OpenScreenPacket.ScreenType screenType() {
        return OpenScreenPacket.ScreenType.WORMHOLE;
    }

    public int getLV() { return 15; }
    public int getCost() { return 30; }
    public int getCooldown() { return 600; }
    @Override
    public AbilityBase getInstance() {
        return new Wormhole();
    }
}
