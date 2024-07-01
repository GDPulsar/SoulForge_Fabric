package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class PerseveranceClaw extends MagicSwordItem {
    public PerseveranceClaw() {
        super(5f, 0.6f, 0.75f);

        addAttribute(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "pv_claw_reach"), -1f, EntityAttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        PlayerEntity player = (PlayerEntity)attacker;
        if (target instanceof PlayerEntity playerTarget) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            SoulComponent targetSoul = SoulForge.getPlayerSoul(playerTarget);
            Utils.addAntiheal(0.4f, playerSoul.getLV()*20f, targetSoul);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
            playerSoul.addTag("immobile");
            playerSoul.setValue("clawGouge", 23);
            user.getItemCooldownManager().set(this, 300);
        }
        return TypedActionResult.pass(stack);
    }
}
