package com.pulsar.soulforge.shield;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public interface ShieldDisabledCallback {
    Event<ShieldDisabledCallback> EVENT = EventFactory.createArrayBacked(ShieldDisabledCallback.class,
            (listeners) -> (defender, hand, shield) -> {
                for (ShieldDisabledCallback listener : listeners) {
                    ActionResult result = listener.disable(defender, hand, shield);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult disable(PlayerEntity defender, Hand hand, ItemStack shield);
}
