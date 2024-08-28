package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ScreenAbilityBase;
import com.pulsar.soulforge.client.networking.OpenScreenPacket;

public class Reload extends ScreenAbilityBase {
    @Override
    public OpenScreenPacket.ScreenType screenType() {
        return OpenScreenPacket.ScreenType.RELOAD;
    }

    public int getLV() { return 12; }
    public int getCost() { return 35; }
    public int getCooldown() { return 300; }
    @Override
    public AbilityBase getInstance() {
        return new Reload();
    }
}
