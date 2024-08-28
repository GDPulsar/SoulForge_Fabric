package com.pulsar.soulforge.ability.perseverance;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ScreenAbilityBase;
import com.pulsar.soulforge.client.networking.OpenScreenPacket;

public class MorphingWeaponry extends ScreenAbilityBase {
    @Override
    public OpenScreenPacket.ScreenType screenType() {
        return OpenScreenPacket.ScreenType.MORPHING_WEAPONRY;
    }

    public int getLV() { return 1; }
    public int getCost() { return 20; }
    public int getCooldown() { return 0; }
    @Override
    public AbilityBase getInstance() {
        return new MorphingWeaponry();
    }
}
