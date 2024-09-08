package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.ValueComponent;
import com.pulsar.soulforge.data.AbilityLayout;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.SoulJarItem;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.ResetData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EndSoulResetPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        PacketByteBuf buffer = PacketByteBufs.create().writeUuid(player.getUuid()).writeString("hey_chat_im_back");
        buffer.writeBoolean(false);
        SoulForgeNetworking.broadcast(null, server, SoulForgeNetworking.PERFORM_ANIMATION, buffer);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        String trait1Str = buf.readString();
        String trait2Str = buf.readString();
        boolean strong = buf.readBoolean();
        boolean pure = buf.readBoolean();
        int lv = buf.readVarInt();
        int exp = buf.readVarInt();
        AbilityLayout newLayout = new AbilityLayout();
        ValueComponent values = SoulForge.getValues(player);
        if (values != null) {
            if (values.getExtraVals().contains("heldJar")) {
                ItemStack soulJar = ItemStack.fromNbt(values.getExtraVals().getCompound("heldJar"));
                for (int slot = 0; slot < player.getInventory().size(); slot++) {
                    if (player.getInventory().getStack(slot).isOf(SoulForgeItems.SOUL_JAR) &&
                        player.getInventory().getStack(slot).getOrCreateNbt().contains("usedInReroll")) {
                        soulJar = player.getInventory().getStack(slot);
                        break;
                    }
                }
                if (SoulJarItem.getHasSoul(soulJar)) {
                    newLayout = SoulJarItem.getLayout(soulJar);
                }
                SoulJarItem.setFromPlayer(soulJar, player);
            }
        }
        playerSoul.softReset();
        List<TraitBase> traits = new ArrayList<>();
        traits.add(Traits.get(trait1Str));
        if (!Objects.equals(trait2Str, "")) {
            traits.add(Traits.get(trait2Str));
        }
        playerSoul.setResetValues(traits, strong, pure);
        playerSoul.setLV(lv);
        playerSoul.setEXP(exp);
        playerSoul.setAbilityLayout(newLayout);
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
        values.removeBool("resettingSoul");
        values.removeBool("Immobilized");
        //SoulForgeNetworking.broadcast(null, server, SoulForgeNetworking.PERFORM_ANIMATION, PacketByteBufs.create().writeUuid(player.getUuid()).writeString("im_going_to_see_mettaton_brb"));
    }
}
