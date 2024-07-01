package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.kindness.PainSplit;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class YourShield extends AbilityBase {
    public boolean pullTarget = false;
    public PlayerEntity target;
    private int fallImmunityTime = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        for (AbilityBase ability : playerSoul.getActiveAbilities()) {
            if (ability instanceof PainSplit painSplit) {
                if (painSplit.target != null) {
                    if (player.isSneaking()) {
                        pullTarget = true;
                        target = painSplit.target;
                        target.setVelocity(player.getPos().subtract(target.getPos()).normalize().multiply(2.5f));
                        target.velocityModified = true;
                        SoulComponent targetSoul = SoulForge.getPlayerSoul(target);
                        targetSoul.addTag("fallImmune");
                        fallImmunityTime = 0;
                    } else {
                        player.setVelocity(target.getPos().subtract(player.getPos()).normalize().multiply(2.5f));
                        player.velocityModified = true;
                    }
                    return super.cast(player);
                }
            }
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (pullTarget)  {
            fallImmunityTime++;
            return fallImmunityTime >= 80;
        }
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        if (pullTarget) {
            SoulComponent targetSoul = SoulForge.getPlayerSoul(target);
            targetSoul.removeTag("fallImmune");
        }
        return super.end(player);
    }

    public int getLV() { return 15; }
    public int getCost() { return 30; }
    public int getCooldown() { return 200; }
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new YourShield();
    }
}
