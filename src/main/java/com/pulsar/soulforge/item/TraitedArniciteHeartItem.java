package com.pulsar.soulforge.item;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TraitedArniciteHeartItem extends Item {
    public final TraitBase trait;

    public TraitedArniciteHeartItem(TraitBase trait) {
        super(new FabricItemSettings());
        this.trait = trait;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
        if ((playerSoul.hasTrait(trait) || playerSoul.hasTrait(Utils.getInvertedVariant(trait)))
                && !user.hasStatusEffect(SoulForgeEffects.MANA_SICKNESS)) {
            if (playerSoul.getMagic() < 100f || (Utils.isInverted(playerSoul) && playerSoul.getMagicGauge() < playerSoul.getMagicGaugeMax())) {
                float adding = Math.min(100f - playerSoul.getMagic(), 100f);
                playerSoul.setMagic(playerSoul.getMagic() + 100f);
                if (Utils.isInverted(playerSoul) && playerSoul.getMagicGauge() < playerSoul.getMagicGaugeMax()) {
                    playerSoul.setMagicGauge(playerSoul.getMagicGauge() + (100f - adding));
                }
                user.giveItemStack(new ItemStack(SoulForgeItems.ARNICITE_HEART));
                user.setCurrentHand(hand);
                user.getStackInHand(hand).decrement(1);
                return TypedActionResult.consume(itemStack);
            }
        }
        return TypedActionResult.fail(itemStack);
    }
}
