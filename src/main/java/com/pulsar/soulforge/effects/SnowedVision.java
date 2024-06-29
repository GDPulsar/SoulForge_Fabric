package com.pulsar.soulforge.effects;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.FogShape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;

public class SnowedVision extends StatusEffect {
    protected SnowedVision() {
        super(StatusEffectCategory.HARMFUL, 0xFFFFFF);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {}
}
