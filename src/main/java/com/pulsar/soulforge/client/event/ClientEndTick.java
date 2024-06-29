package com.pulsar.soulforge.client.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ClientEndTick implements ClientTickEvents.EndTick {
    @Override
    public void onEndTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul != null) {

            }
        }
    }
}
