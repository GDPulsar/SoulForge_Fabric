package com.pulsar.soulforge.util;

import com.pulsar.soulforge.networking.SoulForgeNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;

public class ServerTickManager extends TickManager {
    private final MinecraftServer server;

    public ServerTickManager(MinecraftServer server) {
        this.server = server;
    }
    public void setFrozen(boolean frozen) {
        super.setFrozen(frozen);
        this.sendUpdateTickRatePacket();
    }

    private void sendUpdateTickRatePacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(tickRate);
        buf.writeBoolean(frozen);
        SoulForgeNetworking.broadcast(null, server, SoulForgeNetworking.UPDATE_TICK_RATE, buf);
    }

    public void setTickRate(float tickRate) {
        super.setTickRate(tickRate);
        this.sendUpdateTickRatePacket();
    }
}
