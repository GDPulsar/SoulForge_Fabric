package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

public class AntigravityZone extends ToggleableAbilityBase {
    int timer = 0;
    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (timer % 20 == 0) {
            int affectedCount = 0;
            for (Entity entity : player.getWorld().getOtherEntities(player, Box.of(player.getPos(), 30, 30, 30))) {
                if (entity instanceof LivingEntity target) {
                    if (target.distanceTo(player) < 15f) {
                        affectedCount++;
                    }
                }
            }
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            playerSoul.setStyle(playerSoul.getStyle() + 3 * affectedCount);
        }
        timer = (timer + 1) % 20;
        return super.tick(player);
    }

    public int getLV() { return 17; }

    public int getCost() { return 40; }

    public int getCooldown() { return 400; }

    @Override
    public AbilityBase getInstance() {
        return new AntigravityZone();
    }
}
