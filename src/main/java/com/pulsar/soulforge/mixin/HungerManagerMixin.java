package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
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
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.hasValue("antiheal")) {
            float c = (float)Math.random();
            if (c <= playerSoul.getValue("antiheal")) {
                this.foodTickTimer = 0;
                ci.cancel();
            }
        }
    }
}