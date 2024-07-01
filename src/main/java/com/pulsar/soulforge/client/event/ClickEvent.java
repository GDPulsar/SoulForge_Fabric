package com.pulsar.soulforge.client.event;

import com.pulsar.soulforge.networking.HoldItemPacket;
import com.pulsar.soulforge.networking.LeftClickPacket;
import com.pulsar.soulforge.networking.RightClickPacket;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

public class ClickEvent implements ClientRawInputEvent.MouseClicked {
    @Override
    public EventResult mouseClicked(MinecraftClient client, int button, int action, int mods) {
        if (client.player != null) {
            if (client.currentScreen == null) {
                if (button == 0) {
                    if (action == 1) {
                        ClientPlayNetworking.send(new LeftClickPacket());
                    }
                }
                if (button == 1) {
                    if (action == 1) {
                        ClientPlayNetworking.send(new RightClickPacket());
                    }
                    ClientPlayNetworking.send(new HoldItemPacket(action == 1));
                }
            } else {
                ClientPlayNetworking.send(new HoldItemPacket(false));
            }
        }
        return EventResult.pass();
    }
}
