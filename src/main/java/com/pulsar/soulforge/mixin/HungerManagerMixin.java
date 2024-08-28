package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Shadow private int foodTickTimer;

    @Inject(method = "update", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V"), cancellable = true)
    protected void modifyNaturalPlayerHeal(PlayerEntity player, CallbackInfo ci) {
        if (player.getAttributeValue(SoulForgeAttributes.ANTIHEAL) != 0) {
            float c = (float)Math.random();
            if (c <= player.getAttributeValue(SoulForgeAttributes.ANTIHEAL)) {
                this.foodTickTimer = 0;
                ci.cancel();
            }
        }
    }
}