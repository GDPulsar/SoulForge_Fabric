package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.client.networking.PerformAnimationPacket;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class IntegrityRapier extends MagicSwordItem {
    public IntegrityRapier() {
        super(3, 3f, 1f/7f);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
            if (!playerSoul.hasValue("parryCooldown")) playerSoul.setValue("parryCooldown", 0f);
            if (playerSoul.getValue("parryCooldown") == 0f) {
                user.getItemCooldownManager().set(this, 50);
                playerSoul.setValue("parryCooldown", 50f);
                playerSoul.setValue("parry", 5f);
                SoulForgeNetworking.broadcast(null, user.getServer(), new PerformAnimationPacket(user.getUuid(), "parry", false));
                return TypedActionResult.consume(user.getStackInHand(hand));
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }
}
