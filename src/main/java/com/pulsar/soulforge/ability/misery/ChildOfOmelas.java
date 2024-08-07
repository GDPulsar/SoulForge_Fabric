package com.pulsar.soulforge.ability.misery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;

public class ChildOfOmelas extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult result = Utils.getFocussedEntity(player, 10);
        if (result != null) {
            if (result.getEntity() instanceof LivingEntity living) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                SoulForge.getValues(living).setInt("ChildOfOmelasTimer", playerSoul.getEffectiveLV() * 20);
                return super.cast(player);
            }
        }
        return false;
    }

    public int getLV() { return 20; }

    public int getCost() { return 20; }

    public int getCooldown() { return 1200; }

    public AbilityType getType() { return AbilityType.CAST; }
    @Override
    public AbilityBase getInstance() {
        return new ChildOfOmelas();
    }
}
