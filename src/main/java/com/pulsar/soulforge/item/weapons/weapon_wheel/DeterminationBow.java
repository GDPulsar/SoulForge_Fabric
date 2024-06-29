package com.pulsar.soulforge.item.weapons.weapon_wheel;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.DeterminationArrowProjectile;
import com.pulsar.soulforge.item.weapons.MagicRangedItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class DeterminationBow extends MagicRangedItem {
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(playerEntity);

            int i = this.getMaxUseTime(stack) - remainingUseTicks;
            float f = getPullProgress(i);
            if (!((double)f < 0.1D)) {
                if (!world.isClient) {
                    DeterminationArrowProjectile arrow = new DeterminationArrowProjectile(world, playerEntity);
                    arrow.setPosition(playerEntity.getEyePos());
                    arrow.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, f * 6.0F, 1.0F);
                    arrow.setDamage(playerSoul.getLV()/4f * f);
                    world.spawnEntity(arrow);
                }

                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);

                playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            }
        }
    }

    public static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 10.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.success(itemStack);
    }

    public int getRange() {
        return 20;
    }
}
