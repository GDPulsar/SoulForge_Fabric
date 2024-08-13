package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
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
                float distanceMultiplier = 1f;
                if (entity instanceof PlayerEntity player) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                    if (playerSoul.hasCast("Blinding Snowstorm")) {
                        distanceMultiplier = 2f;
                    }
                }
                fogData.fogStart = 4f * distanceMultiplier;
                fogData.fogEnd = 6f * distanceMultiplier;
            }

            public float applyColorModifier(LivingEntity entity, StatusEffectInstance effect, float f, float tickDelta) {
                return 1f;
            }
        });
    }
}
