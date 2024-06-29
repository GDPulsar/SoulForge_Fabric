package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin {
    @Shadow protected abstract float getAdjustedVolume(float volume, SoundCategory category);

    @Redirect(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundSystem;getAdjustedVolume(FLnet/minecraft/sound/SoundCategory;)F"))
    private float redirectModifyAdjustedVolume(SoundSystem instance, float volume, SoundCategory category, @Local SoundInstance sound) {
        float original = this.getAdjustedVolume(volume, category);
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (player.hasStatusEffect(SoulForgeEffects.SNOWED_VISION)) {
                double distance = player.getPos().distanceTo(new Vec3d(sound.getX(), sound.getY(), sound.getZ()));
                return (float)MathHelper.clampedLerp(original,
                        MathHelper.clampedLerp(original * 0.2f, 0, (distance-15)/5f), (distance-5)/2f);
            }
        }
        return original;
    }
}
