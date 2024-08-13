package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin {
    @Shadow private int duration;
    @Unique
    private float durationDecimal = -1f;

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;updateDuration()I"))
    private int soulforge$modifyEffectDurationDecrease(StatusEffectInstance instance, @Local LivingEntity entity) {
        if (this.duration > (int)(durationDecimal + 1f)) {
            durationDecimal = this.duration;
        }
        if (entity.getAttributeValue(SoulForgeAttributes.EFFECT_DURATION_MULTIPLIER) != 1) {
            this.durationDecimal -= (float) entity.getAttributeValue(SoulForgeAttributes.EFFECT_DURATION_MULTIPLIER);
            this.duration = (int)durationDecimal;
        } else {
            this.duration--;
            this.durationDecimal = this.duration;
        }
        return this.duration;
    }
}
