package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.effects.SoulForgeEffects;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.FogShape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Shadow @Final private static List<BackgroundRenderer.StatusEffectFogModifier> FOG_MODIFIERS;

    static {
        FOG_MODIFIERS.add(new BackgroundRenderer.StatusEffectFogModifier() {
            @Override
            public StatusEffect getStatusEffect() {
                return SoulForgeEffects.SNOWED_VISION;
            }

            @Override
            public void applyStartEndModifier(BackgroundRenderer.FogData fogData, LivingEntity entity, StatusEffectInstance effect, float viewDistance, float tickDelta) {
                fogData.fogStart = 4f;
                fogData.fogEnd = 6f;
            }

            public float applyColorModifier(LivingEntity entity, StatusEffectInstance effect, float f, float tickDelta) {
                return 1f;
            }
        });
    }
}
