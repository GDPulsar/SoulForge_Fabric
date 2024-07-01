package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class FrozenGrasp extends AbilityBase {
    public boolean used = false;
    public LivingEntity target;
    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        used = false;
        target = null;
        timer = 60;
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (target != null && used) {
            timer--;
        }
        return !used && timer <= 0;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        used = false;
        timer = 0;
        target = null;
        return super.end(player);
    }

    public int getLV() { return 5; }
    public int getCost() { return 35; }
    public int getCooldown() { return 500; }
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new FrozenGrasp();
    }
}
