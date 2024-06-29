package com.pulsar.soulforge.client.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class ClientStartTick implements ClientTickEvents.StartTick {
    SoundInstance snowstormSound = null;
    @Override
    public void onStartTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player != null) {
            if (player.hasStatusEffect(SoulForgeEffects.SNOWED_VISION)) {
                if (snowstormSound == null) snowstormSound = new MovingSoundInstance(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.MASTER, Random.create()) {
                    @Override
                    public void tick() {}
                };
                if (!client.getSoundManager().isPlaying(snowstormSound)) {
                    client.getSoundManager().play(snowstormSound);
                }
            } else {
                if (snowstormSound != null && client.getSoundManager().isPlaying(snowstormSound)) {
                    client.getSoundManager().stop(snowstormSound);
                }
            }
        }
    }
}
