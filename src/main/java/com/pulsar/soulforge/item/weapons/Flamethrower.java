package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.particle.SoulForgeParticles;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
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
        if (playerSoul.getMagic() > 2f) {
            if (!world.isClient) {
                for (LivingEntity entity : Utils.getEntitiesInFrontOf(user, 1.5f + playerSoul.getEffectiveLV()/4f, 3f + playerSoul.getEffectiveLV()/2f, 1f, 2f)) {
                    if (entity instanceof PlayerEntity targetPlayer) {
                        if (!TeamUtils.canDamageEntity(user.getServer(), user, targetPlayer)) continue;
                    }
                    if (entity.damage(SoulForgeDamageTypes.of(user, world, SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), 2f + playerSoul.getEffectiveLV() / 4f)) {
                        playerSoul.setStyle(playerSoul.getStyle() + 1);
                    }
                    if (entity.getFireTicks() < 40) {
                        entity.setFireTicks(50);
                    }
                }
                world.playSoundFromEntity(null, user, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1f, 1f);
                playerSoul.setMagic(playerSoul.getMagic() - 2f);
                playerSoul.resetLastCastTime();
            } else {
                Vec3d handPos = Utils.getArmPosition(user);
                for (int i = 0; i < 10; i++) {
                    world.addParticle(SoulForgeParticles.FIRE_PARTICLE,
                            handPos.x, handPos.y, handPos.z,
                            (user.getRotationVector().x + Math.random() / 5f - 0.1f) * 7f, Math.random() / 2f - 0.25f, (user.getRotationVector().z + Math.random() / 5f - 0.1f) * 7f);
                }
            }
        }
        return TypedActionResult.consume(user.getStackInHand(hand));
    }
}
