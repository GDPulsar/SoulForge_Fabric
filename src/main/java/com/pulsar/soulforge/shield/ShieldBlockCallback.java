package com.pulsar.soulforge.shield;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public interface ShieldBlockCallback {
    Event<ShieldBlockCallback> EVENT = EventFactory.createArrayBacked(ShieldBlockCallback.class,
            (listeners) -> (defender, source, amount, hand, shield) -> {
                for (ShieldBlockCallback listener : listeners) {
                    ActionResult result = listener.block(defender, source, amount, hand, shield);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult block(LivingEntity defender, DamageSource source, float amount, Hand hand, ItemStack shield);
}
