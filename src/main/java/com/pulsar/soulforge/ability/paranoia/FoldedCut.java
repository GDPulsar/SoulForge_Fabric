package com.pulsar.soulforge.ability.paranoia;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import net.minecraft.server.network.ServerPlayerEntity;

public class FoldedCut extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {

        return super.cast(player);
    }

    @Override
    public int getLV() {
        return 20;
    }

    @Override
    public int getCost() {
        return 40;
    }

    @Override
    public int getCooldown() {
        return 400;
    }

    @Override
    public AbilityType getType() {
        return AbilityType.CAST;
    }

    @Override
    public AbilityBase getInstance() {
        return new FoldedCut();
    }
}
