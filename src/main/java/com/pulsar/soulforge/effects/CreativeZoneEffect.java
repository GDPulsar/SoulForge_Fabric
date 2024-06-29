package com.pulsar.soulforge.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.Iterator;
import java.util.Map;

public class CreativeZoneEffect extends StatusEffect {
    public CreativeZoneEffect() {
        super(
                StatusEffectCategory.BENEFICIAL,
                0xAAFFFF
        );
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            if (!player.isCreative() && !player.isSpectator()) {
                //NbtCompound tag = player.writeNbt();
            }
            player.getAbilities().allowFlying = true;
            player.sendAbilitiesUpdate();
        }
    }

    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            player.getAbilities().allowFlying = false;
            player.sendAbilitiesUpdate();
        }
    }
}
