package com.pulsar.soulforge.ability.perseverance;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.shader.TestPostProcessor;
import net.minecraft.server.network.ServerPlayerEntity;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

public class Furioso extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (player.isSneaking()) TestPostProcessor.INSTANCE.setActive(!TestPostProcessor.INSTANCE.isActive());
        else {
            ScreenshakeInstance screenshake = new ScreenshakeInstance(50);
            screenshake.setEasing(Easing.SINE_IN_OUT);
            screenshake.setIntensity(0f, 50f, 0f);
            ScreenshakeHandler.addScreenshake(screenshake);
        }
        return false;
    }

    public int getLV() { return 20; }

    public int getCost() { return 100; }

    public int getCooldown() { return 6000; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Furioso();
    }
}
