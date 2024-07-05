package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.entity.DeterminationPlatformEntity;
import net.minecraft.entity.player.PlayerEntity;

public class DeterminationPlatform extends ToggleableAbilityBase {
    public void spawn(PlayerEntity player) {
        DeterminationPlatformEntity platform = new DeterminationPlatformEntity(player.getWorld(), player.getPos().subtract(0, 0.25f, 0));
        player.getWorld().spawnEntity(platform);
        player.teleport(platform.getX(), platform.getY()+0.25f, platform.getZ());
        player.fallDistance = 0;
    }

    public int getLV() { return 3; }

    public int getCost() { return 0; }

    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new DeterminationPlatform();
    }
}
