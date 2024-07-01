package com.pulsar.soulforge.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class CreativeZoneEffect extends StatusEffect {
    public CreativeZoneEffect() {
        super(
                StatusEffectCategory.BENEFICIAL,
                0xAAFFFF
        );
    }

    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            player.getAbilities().allowFlying = true;
            player.sendAbilitiesUpdate();
        }
        return true;
    }

    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            player.getAbilities().allowFlying = false;
            player.sendAbilitiesUpdate();
        }
    }
}
