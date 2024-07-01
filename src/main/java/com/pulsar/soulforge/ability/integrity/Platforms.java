package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.entity.IntegrityPlatformEntity;
import net.minecraft.entity.player.PlayerEntity;

public class Platforms extends ToggleableAbilityBase {
    public void spawn(PlayerEntity player) {
        IntegrityPlatformEntity platform = new IntegrityPlatformEntity(player.getWorld(), player.getPos().subtract(0, 0.25f, 0));
        player.getWorld().spawnEntity(platform);
        player.setPos(platform.getX(), platform.getY()+0.25f, platform.getZ());
        player.fallDistance = 0;
    }

    public int getLV() { return 1; }
    public int getCost() { return 0; }
    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new Platforms();
    }
}
