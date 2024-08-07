package com.pulsar.soulforge.ability.misery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class Pestilence extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        for (PlayerEntity target : player.getServerWorld().getPlayers((target) -> target.hasStatusEffect(SoulForgeEffects.MANA_TUMOR))) {
            if (target.hasStatusEffect(SoulForgeEffects.MANA_TUMOR)) {
                StatusEffectInstance tumor = target.getStatusEffect(SoulForgeEffects.MANA_TUMOR);
                if (tumor.getAmplifier() < 2) {
                    target.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_TUMOR, (int)Math.floor((Math.random() + 1) * 72000), 2));
                    SoulForge.getValues(target).setBool("PestilenceTumor", true);
                }
            }
        }
        return super.cast(player);
    }

    public int getLV() { return 20; }

    public int getCost() { return 20; }

    public int getCooldown() { return 1200; }

    public AbilityType getType() { return AbilityType.CAST; }
    @Override
    public AbilityBase getInstance() {
        return new Pestilence();
    }
}
