package com.pulsar.soulforge.effects;

import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class EepyEffect extends StatusEffect {
    public EepyEffect() {
        super(
                StatusEffectCategory.HARMFUL,
                0x2e0a61
        );
        this.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "ab7c8dfa-e8c0-4eb1-a7ad-e33a34b180cd", -0.02, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS, "ab7c8dfa-e8c0-4eb1-a7ad-e33a34b180cd", -0.02, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "ab7c8dfa-e8c0-4eb1-a7ad-e33a34b180cd", -0.01, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "ab7c8dfa-e8c0-4eb1-a7ad-e33a34b180cd", -0.01, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        entity.wakeUp();
        super.onRemoved(entity, attributes, amplifier);
    }
}
