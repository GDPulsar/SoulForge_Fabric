package com.pulsar.soulforge.effects;

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
        this.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "88520988-2817-4564-8d63-5de49b56cd43", -0.01, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "2b619887-378d-4f9e-8161-15e0a2ab72eb", -0.01, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        entity.wakeUp();
        super.onRemoved(entity, attributes, amplifier);
    }
}
