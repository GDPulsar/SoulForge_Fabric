package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.effects.SoulForgeEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SleepingChatScreen.class)
public class SleepingChatScreenMixin {
    @Inject(method = "stopSleeping", at = @At("HEAD"), cancellable = true)
    private void soulforge$preventStopSleeping(CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (player.hasStatusEffect(SoulForgeEffects.EEPY)) {
                ci.cancel();
            }
        }
    }
}
