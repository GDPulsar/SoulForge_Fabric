package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.effects.SoulForgeEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.MilkBucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(MilkBucketItem.class)
public class MilkBucketItemMixin {
    @Redirect(method = "finishUsing", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;clearStatusEffects()Z"))
    private boolean modifyClearStatusEffects(LivingEntity instance) {
        if (instance.hasStatusEffect(SoulForgeEffects.MANA_SICKNESS) ||
            instance.hasStatusEffect(SoulForgeEffects.MANA_OVERLOAD) ||
            instance.hasStatusEffect(SoulForgeEffects.MANA_TUMOR)) {
            boolean hasEffects = !instance.getStatusEffects().isEmpty();
            List<StatusEffectInstance> effects = List.copyOf(instance.getStatusEffects());
            for (StatusEffectInstance effect : effects) {
                if (effect.getEffectType() == SoulForgeEffects.MANA_SICKNESS) continue;
                if (effect.getEffectType() == SoulForgeEffects.MANA_OVERLOAD) continue;
                if (effect.getEffectType() == SoulForgeEffects.MANA_TUMOR) continue;
                instance.removeStatusEffect(effect.getEffectType());
            }
            return hasEffects;
        }
        return instance.clearStatusEffects();
    }
}
