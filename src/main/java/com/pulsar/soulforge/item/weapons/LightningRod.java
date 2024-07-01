package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.LightningRodProjectile;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class LightningRod extends MagicSwordItem {
    public LightningRod() {
        super(4, 1.4f, 0.33f);
        addAttribute(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE, new EntityAttributeModifier( Identifier.of(SoulForge.MOD_ID, "lightning_rod_reach"), 2f, EntityAttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return stack.getOrDefault(SoulForgeItems.THROWN_COMPONENT, false) ? 1 : 72000;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return;
        }
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        if (i < 10) {
            return;
        }
        if (!world.isClient) {
            LightningRodProjectile projectile = new LightningRodProjectile(world, playerEntity);
            projectile.setOwner(playerEntity);
            projectile.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0f, 2.5f, 0.0f);
            world.spawnEntity(projectile);
            world.playSoundFromEntity(null, projectile, SoundEvents.ITEM_TRIDENT_THROW.value(), SoundCategory.PLAYERS, 1.0f, 1.0f);
            stack.decrement(1);
        }
        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }
}
