package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;

public class Nanomachines extends AbilityBase {
    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        timer = 1000;
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.HEAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (timer > 0) timer--;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        int rate = Math.round(120f/(Math.min(36, playerSoul.getEffectiveLV() + 6)));
        if (timer % rate * 2 == 0) {
            if (player.getHealth() < player.getMaxHealth()) playerSoul.setStyle(playerSoul.getStyle() + 1);
            player.heal(1f);
        }
        return timer <= 0;
    }

    public int getLV() { return 10; }

    public int getCost() { return 50; }

    public int getCooldown() { return 2000; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Nanomachines();
    }
}
