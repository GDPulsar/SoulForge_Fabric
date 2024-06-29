package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
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
                PacketByteBuf buf = PacketByteBufs.create().writeUuid(user.getUuid()).writeString("parry");
                buf.writeBoolean(false);
                SoulForgeNetworking.broadcast(null, user.getServer(), SoulForgeNetworking.PERFORM_ANIMATION, buf);
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
