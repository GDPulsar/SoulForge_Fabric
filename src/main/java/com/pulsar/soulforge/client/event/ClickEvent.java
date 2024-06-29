package com.pulsar.soulforge.client.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.determination.WeaponWheel;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.client.ui.WeaponWheelScreen;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.trait.Traits;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

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
