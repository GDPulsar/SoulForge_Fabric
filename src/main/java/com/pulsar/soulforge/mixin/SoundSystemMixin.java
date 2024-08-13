package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.SoulForgeClient;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin {
    @ModifyExpressionValue(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundSystem;getAdjustedVolume(FLnet/minecraft/sound/SoundCategory;)F"))
    private float soulforge$modifyAdjustedVolume(float original, @Local SoundInstance sound) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        float multiplier = 1f;
        if (player != null) {
            if (player.hasStatusEffect(SoulForgeEffects.SNOWED_VISION) && sound != SoulForgeClient.snowstormSound) {
                double distance = player.getPos().distanceTo(new Vec3d(sound.getX(), sound.getY(), sound.getZ()));
                multiplier *= (float)MathHelper.clampedLerp(1f,
                        MathHelper.clampedLerp(0.2f, 0, (distance-15)/5f), (distance-5)/2f);
            }
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasCast("Proceed") && sound != SoulForgeClient.heartbeatSound) {
                multiplier *= 0.05f;
            }
        }
        return original * multiplier;
    }
}
