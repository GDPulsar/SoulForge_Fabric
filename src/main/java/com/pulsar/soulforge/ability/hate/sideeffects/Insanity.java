package com.pulsar.soulforge.ability.hate.sideeffects;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.SideEffectAbilityBase;
import net.minecraft.server.network.ServerPlayerEntity;

public class Insanity extends SideEffectAbilityBase {
    @Override
    public float getOccurrenceChance() {
        return 0.01f;
    }

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulForge.LOGGER.info("YOU ARE INSANE WOAJIDOIAJWDIOJAOWDIJAOWIDJAOWIJD");
        return super.cast(player);
    }

    @Override
    public AbilityBase getInstance() {
        return new Insanity();
    }
}
