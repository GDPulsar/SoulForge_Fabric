package com.pulsar.soulforge.ability;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

public abstract class ToggleableAbilityBase extends AbilityBase {
    @Override
    public AbilityType getType() {
        return AbilityType.TOGGLE;
    }

    public boolean cast(ServerPlayerEntity player) {
        setActive(true);
        return true;
    }
    public boolean tick(ServerPlayerEntity player) {
        return !isActive();
    }
    public boolean end(ServerPlayerEntity player) {
        setActive(false);
        setLastCast(Objects.requireNonNull(player.getServer()).getTicks());
        return true;
    }
}
