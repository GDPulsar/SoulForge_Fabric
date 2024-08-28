package com.pulsar.soulforge.client.networking;

import com.pulsar.soulforge.client.ui.*;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class OpenScreenPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int screenId = buf.readVarInt();
        client.execute(() -> {
            switch (screenId) {
                case 0 -> client.setScreen(new WeaponWheelScreen());
                case 1 -> client.setScreen(new WormholeScreen());
                case 2 -> client.setScreen(new ArmoryScreen());
                case 3 -> client.setScreen(new ReloadScreen());
                case 4 -> client.setScreen(new MorphingWeaponryScreen());
                case 5 -> client.setScreen(new RampageScreen());
            }
        });
    }

    public enum ScreenType {
        WEAPON_WHEEL(0),
        WORMHOLE(1),
        ARMORY(2),
        RELOAD(3),
        MORPHING_WEAPONRY(4),
        RAMPAGE(5);

        final int val;
        ScreenType(int val) {
            this.val = val;
        }

        public int val() {
            return this.val;
        }
    }
}
