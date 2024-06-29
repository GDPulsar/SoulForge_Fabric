package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Flamethrower extends MagicItem {

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.soulforge.flamethrower.tooltip"));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
        boolean frostburn = playerSoul.getTraits().contains(Traits.bravery) && playerSoul.getTraits().contains(Traits.patience);
        if (playerSoul.getMagic() > 2f) {
            if (!world.isClient) {
                for (LivingEntity entity : Utils.getEntitiesInFrontOf(user, 1.5f + playerSoul.getEffectiveLV()/4f, 3f + playerSoul.getEffectiveLV()/2f, 1f, 2f)) {
                    if (entity instanceof PlayerEntity targetPlayer) {
                        if (!TeamUtils.canDamagePlayer(user.getServer(), user, targetPlayer)) continue;
                    }
                    entity.damage(user.getDamageSources().playerAttack(user), 2f);
                    if (frostburn) {
                        entity.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.FROSTBURN, 50, 0));
                    } else {
                        if (entity.getFireTicks() < 40) {
                            entity.setFireTicks(50);
                        }
                    }
                }
                world.playSoundFromEntity(null, user, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1f, 1f);
                playerSoul.setMagic(playerSoul.getMagic() - 2f);
                playerSoul.resetLastCastTime();
            } else {
                for (int i = 0; i < 5; i++) {
                    world.addParticle(ParticleTypes.FLAME,
                            user.getPos().x, user.getPos().y+0.5f, user.getPos().z,
                            (user.getRotationVector().x + Math.random() / 2f - 0.25f) / 4f, Math.random() / 8f, (user.getRotationVector().z + Math.random() / 2f - 0.25f) / 4f);
                }
            }
        }
        return TypedActionResult.consume(user.getStackInHand(hand));
    }
}
