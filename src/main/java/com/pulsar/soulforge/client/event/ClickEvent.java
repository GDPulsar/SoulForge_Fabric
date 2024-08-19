package com.pulsar.soulforge.client.event;

import com.pulsar.soulforge.networking.SoulForgeNetworking;
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
                        ClientPlayNetworking.send(SoulForgeNetworking.LEFT_CLICK, PacketByteBufs.create());
                    }
                }
                if (button == 1) {
                    if (action == 1) {
                        ClientPlayNetworking.send(SoulForgeNetworking.RIGHT_CLICK, PacketByteBufs.create());
                    }
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBoolean(action == 1);
                    ClientPlayNetworking.send(SoulForgeNetworking.HOLD_ITEM, buf);
                }
            } else {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(false);
                ClientPlayNetworking.send(SoulForgeNetworking.HOLD_ITEM, buf);
            }
        }
        return EventResult.pass();
    }
}
