package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.PolarityBall;
import net.minecraft.server.network.ServerPlayerEntity;

public class Polarities extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        PolarityBall polarityBall = new PolarityBall(player.getWorld(), player, player.isSneaking());
        polarityBall.setPosition(player.getEyePos());
        polarityBall.setVelocity(player.getRotationVector().multiply(1.5f));
        polarityBall.setInverse(player.isSneaking());
        player.getWorld().spawnEntity(polarityBall);
        return super.cast(player);
    }

    public String getName() { return "Polarities"; }

    public int getLV() { return 12; }

    public int getCost() { return 50; }

    public int getCooldown() { return 300; }

    @Override
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Polarities();
    }
}
