package com.pulsar.soulforge.item.weapons.weapon_wheel;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.DeterminationSpearProjectile;
import com.pulsar.soulforge.entity.SOJProjectile;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.weapons.MagicSwordItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class DeterminationSpear extends MagicSwordItem {
    public DeterminationSpear() {
        super(6, 1.4f, 0.25f);
        addAttribute(ReachEntityAttributes.ATTACK_RANGE, new EntityAttributeModifier("dt_spear_reach", 2f, EntityAttributeModifier.Operation.ADDITION));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return;
        }
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        if (i < 10) {
            return;
        }
        if (!world.isClient) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul((PlayerEntity) user);
            if (user.isSneaking() && playerSoul.getMagic() >= 100f) {
                SOJProjectile projectile = new SOJProjectile(world, playerEntity);
                projectile.setOwner(playerEntity);
                projectile.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0f, 5f, 0f);
                world.spawnEntity(projectile);
                world.playSoundFromEntity(null, projectile, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 4.0f, 0.75f);
                playerSoul.setMagic(0f);
                playerSoul.resetLastCastTime();
                user.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 3000, 0));
            } else {
                DeterminationSpearProjectile projectile = new DeterminationSpearProjectile(world, playerEntity);
                projectile.setOwner(playerEntity);
                projectile.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0f, 2.5f, 1.0f);
                world.spawnEntity(projectile);
                world.playSoundFromEntity(null, projectile, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
            playerSoul.removeWeapon();
            if (playerSoul.getMagic() >= 5f) {
                playerSoul.setMagic(playerSoul.getMagic() - 5f);
                playerSoul.resetLastCastTime();
                playerSoul.setWeapon(new ItemStack(SoulForgeItems.DETERMINATION_SPEAR));
            }
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
