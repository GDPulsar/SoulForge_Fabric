package com.pulsar.soulforge.mixin;

import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AllayEntity.class)
public class AllayEntityMixin {
    @Inject(method = "interactMob", at = @At(value = "HEAD"), cancellable = true)
    private void soulforge$allaySummonWeaponFix(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (player.getInventory().selectedSlot == 9 && hand == Hand.MAIN_HAND) cir.setReturnValue(ActionResult.PASS);
    }
}
