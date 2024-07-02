package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mixin(StatusEffect.class)
public abstract class StatusEffectMixin {
    @Shadow public abstract String getTranslationKey();

    @Shadow @Final private Map<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    @Inject(method = "applyUpdateEffect", at=@At("HEAD"), cancellable = true)
    public void modifyApplyEffect(LivingEntity entity, int amplifier, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasValue("antiheal")) {
                if ((StatusEffect) (Object) this == StatusEffects.REGENERATION) {
                    if (Math.random() <= playerSoul.getValue("antiheal")) {
                        ci.cancel();
                    }
                }
            }
        }
    }

    @ModifyArg(method = "applyInstantEffect", at=@At(value = "INVOKE", target="Lnet/minecraft/entity/LivingEntity;heal(F)V"))
    public float modifyInstantHealth(float amount, @Local LivingEntity target) {
        if (target instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasValue("antiheal")) {
                return amount * (1f - playerSoul.getValue("antiheal"));
            }
        }
        return amount;
    }

    @Inject(method = "addAttributeModifier", at=@At("HEAD"), cancellable = true)
    public void addAttributeModifier(EntityAttribute attribute, String uuid, double amount, EntityAttributeModifier.Operation operation, CallbackInfoReturnable<StatusEffect> cir) {
        if (Objects.equals(uuid, "91AEAA56-376B-4498-935B-2F7F68070635") || // speed
            Objects.equals(uuid, "7107DE5E-7CE8-4030-940E-514C1F160890")) { // slowness
            EntityAttributeModifier speedModifier = new EntityAttributeModifier(UUID.fromString(uuid), this::getTranslationKey, MathHelper.sign(amount) * 0.1f, EntityAttributeModifier.Operation.MULTIPLY_BASE);
            this.attributeModifiers.put(attribute, speedModifier);
            this.attributeModifiers.put(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS, speedModifier);
            cir.setReturnValue((StatusEffect)(Object)this);
        }
        if (Objects.equals(uuid, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9") || // strength
            Objects.equals(uuid, "22653B89-116E-49DC-9B6B-9971489B5BE5")) { // weakness
            cir.setReturnValue((StatusEffect)(Object)this);
        }
    }
}
