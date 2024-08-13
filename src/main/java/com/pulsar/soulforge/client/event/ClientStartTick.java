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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;

import static com.pulsar.soulforge.SoulForgeClient.heartbeatSound;
import static com.pulsar.soulforge.SoulForgeClient.snowstormSound;

public class ClientStartTick implements ClientTickEvents.StartTick {
    @Override
    public void onStartTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (player.hasStatusEffect(SoulForgeEffects.SNOWED_VISION)) {
                if (snowstormSound == null) snowstormSound = new MovingSoundInstance(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.MASTER, Random.create()) {
                    @Override
                    public void tick() {
                        if (!player.isRemoved() && player.hasStatusEffect(SoulForgeEffects.SNOWED_VISION)) {
                            this.x = player.getX();
                            this.y = player.getY();
                            this.z = player.getZ();
                        }
                    }
                };
                if (!client.getSoundManager().isPlaying(snowstormSound)) {
                    client.getSoundManager().play(snowstormSound);
                }
            } else {
                if (snowstormSound != null && client.getSoundManager().isPlaying(snowstormSound)) {
                    client.getSoundManager().stop(snowstormSound);
                    snowstormSound = null;
                }
            }

            if (playerSoul.hasCast("Proceed")) {
                if (heartbeatSound == null) {
                    heartbeatSound = new PositionedSoundInstance(SoulForgeSounds.TRANCE_HEARTBEAT_EVENT, SoundCategory.MASTER, 1f, 1f, Random.create(), player.getBlockPos());
                }
                if (!client.getSoundManager().isPlaying(heartbeatSound)) {
                    client.getSoundManager().play(heartbeatSound);
                }
            } else {
                if (heartbeatSound != null && client.getSoundManager().isPlaying(heartbeatSound)) {
                    client.getSoundManager().stop(heartbeatSound);
                    heartbeatSound = null;
                }
            }

            if (playerSoul.magicModeActive()) {
                AbilityBase currentAbility = playerSoul.getAbilityLayout().getSlot(playerSoul.getAbilityRow(), playerSoul.getAbilitySlot());
                if (currentAbility != null) {
                    currentAbility.displayTick(player);
                }
            }
        }
    }
}
