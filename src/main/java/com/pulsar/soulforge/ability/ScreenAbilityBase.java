package com.pulsar.soulforge.ability;

import com.pulsar.soulforge.client.networking.OpenScreenPacket;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ScreenAbilityBase extends AbilityBase {
    public abstract OpenScreenPacket.ScreenType screenType();
    @Override
    public boolean cast(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(screenType().val());
        ServerPlayNetworking.send(player, SoulForgeNetworking.OPEN_SCREEN, buf);
        return super.cast(player);
    }

    public AbilityType getType() { return AbilityType.CAST; }
}
