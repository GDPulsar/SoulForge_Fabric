package com.pulsar.soulforge.item;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.trait.TraitBase;
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
        if (playerSoul.getTraits().contains(trait) && !user.hasStatusEffect(SoulForgeEffects.MANA_OVERLOAD)) {
            if (playerSoul.getMagic() < 100f) {
                playerSoul.setMagic(100f);
                user.giveItemStack(new ItemStack(SoulForgeItems.ARNICITE_HEART));
                user.setCurrentHand(hand);
                user.getStackInHand(hand).decrement(1);
                return TypedActionResult.consume(itemStack);
            }
        }
        return TypedActionResult.fail(itemStack);
    }
}
