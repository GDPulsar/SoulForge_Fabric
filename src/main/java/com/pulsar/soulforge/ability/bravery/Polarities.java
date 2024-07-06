package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.PolarityBallEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;

public class Polarities extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        PolarityBallEntity polarityBallEntity = new PolarityBallEntity(player.getWorld(), player, player.isSneaking());
        polarityBallEntity.setPosition(player.getEyePos());
        polarityBallEntity.setVelocity(player.getRotationVector().multiply(1.5f));
        polarityBallEntity.setInverse(player.isSneaking());
        player.getWorld().spawnEntity(polarityBallEntity);
        player.getServerWorld().playSoundFromEntity(null, player, SoulForgeSounds.PELLET_SUMMON_EVENT, SoundCategory.PLAYERS, 1f, 0.6f);
        return super.cast(player);
    }

    public int getLV() { return 12; }

    public int getCost() { return 50; }

    public int getCooldown() { return 300; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Polarities();
    }
}
