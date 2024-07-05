package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;

public class Regeneration extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        float healAmount = playerSoul.getEffectiveLV();
        if (playerSoul.hasValue("antiheal")) {
            healAmount *= 1f-playerSoul.getValue("antiheal");
        }
        player.setHealth(player.getHealth() + healAmount);
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_HEAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        for (int i = 0; i < 12; i++) {
            for (int j = -2; j < 3; j++) {
                double angle = (i/6f)*Math.PI;
                double posX = Math.sin(angle) + player.getX();
                double posY = j*0.1 + player.getY() + 1;
                double posZ = Math.cos(angle) + player.getZ();
                double velX = Math.sin(angle)*4;
                double velY = 0;
                double velZ = Math.cos(angle)*4;
                player.getServerWorld().spawnParticles(new DustParticleEffect(DustParticleEffect.RED, 1f), posX, posY, posZ, 2, velX, velY, velZ, 0f);
            }
        }
        return super.cast(player);
    }

    public int getLV() { return 7; }

    public int getCost() { return 50; }

    public int getCooldown() { return 500; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Regeneration();
    }
}
