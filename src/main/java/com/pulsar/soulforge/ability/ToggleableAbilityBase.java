package com.pulsar.soulforge.ability;

import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ToggleableAbilityBase extends AbilityBase {

    public boolean cast(ServerPlayerEntity player) {
        setActive(!getActive());
        return true;
    }
    public boolean tick(ServerPlayerEntity player) {
        return !getActive();
    }
    public boolean end(ServerPlayerEntity player) {
        setActive(false);
        setLastCastTime(player.getServer().getTicks());
        return true;
    }
    @Override
    public AbilityType getType() {
        return AbilityType.TOGGLE;
    }
}
