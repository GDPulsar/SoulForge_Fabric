package com.pulsar.soulforge.ability.fear;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.FearBombEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

public class Detonate extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        for (FearBombEntity bomb : player.getWorld().getEntitiesByClass(FearBombEntity.class, Box.of(player.getPos(), 200, 200, 200), bomb -> bomb.getOwner() == player)) {
            bomb.setDetonatingTimer((int)(Math.random()*20) + 5);
            bomb.setDetonating(true);
        }
        return super.cast(player);
    }

    public int getLV() { return 20; }

    public int getCost() { return 0; }

    public int getCooldown() { return 5; }

    public AbilityType getType() { return AbilityType.CAST; }
    @Override
    public AbilityBase getInstance() {
        return new Detonate();
    }
}
