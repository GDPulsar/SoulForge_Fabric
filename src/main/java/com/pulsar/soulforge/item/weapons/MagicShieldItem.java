package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.shield.CustomShield;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class MagicShieldItem extends MagicItem implements CustomShield {
    private int coolDownTicks;

    public MagicShieldItem(int coolDownTicks) {
        super();

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            this.RegisterModelPredicate();
        }

        this.coolDownTicks = coolDownTicks;
    }

    private void RegisterModelPredicate() {
        ModelPredicateProviderRegistry.register(Identifier.of("blocking"), (itemStack, clientWorld, livingEntity, i) ->
                livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1f : 0f);
    }

    @Override
    public int getCoolDownTicks() {
        return coolDownTicks;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    public void setCoolDownTicks(int coolDownTicks) {
        this.coolDownTicks = coolDownTicks;
    }
}
