package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.advancement.SoulForgeCriterions;
import com.pulsar.soulforge.client.networking.PerformAnimationPacket;
import com.pulsar.soulforge.components.AbilityLayout;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.ResetData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public record EndSoulResetPacket(String trait1, String trait2, Boolean strong, Boolean pure) implements CustomPayload {
    public static final CustomPayload.Id<EndSoulResetPacket> ID = new Id<>(SoulForgeNetworking.END_SOUL_RESET);
    public static final PacketCodec<RegistryByteBuf, EndSoulResetPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, EndSoulResetPacket::trait1,
            PacketCodecs.STRING, EndSoulResetPacket::trait2,
            PacketCodecs.BOOL, EndSoulResetPacket::strong,
            PacketCodecs.BOOL, EndSoulResetPacket::pure,
            EndSoulResetPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(EndSoulResetPacket packet, ServerPlayNetworking.Context context) {
        SoulForgeNetworking.broadcast(null, context.server(), new PerformAnimationPacket(context.player().getUuid(), "hey_chat_im_back", false));
        SoulComponent playerSoul = SoulForge.getPlayerSoul(context.player());
        playerSoul.softReset();
        List<TraitBase> traits = new ArrayList<>();
        traits.add(Traits.get(packet.trait1()));
        String trait2Str = packet.trait2();
        if (!Objects.equals(trait2Str, "")) {
            traits.add(Traits.get(trait2Str));
        }
        boolean strong = packet.strong();
        boolean pure = packet.pure();
        playerSoul.setResetValues(traits, strong, pure);
        ResetData resetData = playerSoul.getResetData();
        if (!traits.contains(Traits.determination)) resetData.resetsSinceDT++;
        else resetData.resetsSinceDT = 0;
        if (traits.size() != 2) resetData.resetsSinceDual++;
        else resetData.resetsSinceDual = 0;
        if (!strong) resetData.resetsSinceStrong++;
        else resetData.resetsSinceStrong = 0;
        if (!pure) resetData.resetsSincePure++;
        else resetData.resetsSincePure = 0;
        resetData.totalResets++;
        if (traits.size() == 1) {
            if (traits.get(0) == Traits.bravery) resetData.bravery = true;
            if (traits.get(0) == Traits.justice) resetData.justice = true;
            if (traits.get(0) == Traits.kindness) resetData.kindness = true;
            if (traits.get(0) == Traits.patience) resetData.patience = true;
            if (traits.get(0) == Traits.integrity) resetData.integrity = true;
            if (traits.get(0) == Traits.perseverance) resetData.perseverance = true;
            if (traits.get(0) == Traits.determination) resetData.determination = true;
        } else if (traits.size() == 2) {
            if (strong) resetData.strongDual = true;
            resetData.addDual(traits.get(0), traits.get(1));
        }
        playerSoul.removeTag("resettingSoul");
        playerSoul.removeTag("immobile");
        //SoulForgeNetworking.broadcast(null, server, SoulForgeNetworking.PERFORM_ANIMATION, PacketByteBufs.create().writeUuid(player.getUuid()).writeString("im_going_to_see_mettaton_brb"));
    }
}
