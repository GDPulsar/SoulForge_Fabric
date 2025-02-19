package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.effects.SoulForgeEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets="net/minecraft/server/network/ServerPlayNetworkHandler$1")
public class PlayerInteractEntityC2SPacketHandlerMixin {
    @Shadow @Final Entity field_28962;
    @Shadow @Final ServerPlayNetworkHandler field_28963;

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void soulforge$immobilizationAttackSelf(CallbackInfo ci) {
        if (this.field_28962 instanceof LivingEntity living) {
            if (living == this.field_28963.player && living.hasStatusEffect(SoulForgeEffects.IMMOBILIZED)) {
                this.field_28963.player.attack(this.field_28963.player);
                ci.cancel();
            }
        }
    }
}
