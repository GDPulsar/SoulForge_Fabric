package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.EntityInitializer;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.TemporaryModifierComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class FrostWave extends MagicItem {
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
        if (playerSoul.getMagic() >= 2f) {
            if (!world.isClient) {
                for (LivingEntity entity : Utils.getEntitiesInFrontOf(user, 3f, 7f, 1f, 2f)) {
                    if (entity instanceof PlayerEntity targetPlayer) {
                        if (!TeamUtils.canDamageEntity(user.getServer(), user, targetPlayer)) continue;
                    }
                    TemporaryModifierComponent modifiers = EntityInitializer.TEMPORARY_MODIFIERS.get(entity);
                    modifiers.addTemporaryModifier(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS, new EntityAttributeModifier(
                            UUID.fromString("ddb1b638-d8ac-47d1-9514-c9ec492e4b34"), "frost_wave",
                            -0.01f * playerSoul.getEffectiveLV(), EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                    ), playerSoul.getEffectiveLV() * 20 + 80);
                    modifiers.addTemporaryModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(
                            UUID.fromString("ddb1b638-d8ac-47d1-9514-c9ec492e4b34"), "frost_wave",
                            -0.01f * playerSoul.getEffectiveLV(), EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                    ), playerSoul.getEffectiveLV() * 20 + 80);
                    int duration = 20;
                    if (entity.hasStatusEffect(SoulForgeEffects.FROSTBITE))
                        duration = entity.getStatusEffect(SoulForgeEffects.FROSTBITE).getDuration() + 20;
                    entity.addStatusEffect(new StatusEffectInstance(
                            SoulForgeEffects.FROSTBITE,
                            duration, 0));
                    if (entity.damage(SoulForgeDamageTypes.of(user, SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), playerSoul.getEffectiveLV() / 5f)) {
                        entity.takeKnockback(0.5f, MathHelper.sin(user.getYaw() * 0.017453292F), -MathHelper.cos(user.getYaw() * 0.017453292F));
                        playerSoul.setStyle(playerSoul.getStyle() + (int) ((playerSoul.getEffectiveLV() / 20f) * (1f + Utils.getTotalDebuffLevel(entity) / 10f)));
                    }
                }
                world.playSoundFromEntity(null, user, SoulForgeSounds.FROST_WAVE_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                playerSoul.setMagic(playerSoul.getMagic() - 2f);
                playerSoul.resetLastCastTime();
            } else {
                Vec3d handPos = Utils.getArmPosition(user);
                for (int i = 0; i < 5; i++) {
                    world.addParticle(ParticleTypes.SNOWFLAKE,
                            handPos.x, handPos.y, handPos.z,
                            (user.getRotationVector().x + Math.random() / 2f - 0.25f) / 4f, Math.random() / 8f, (user.getRotationVector().z + Math.random() / 2f - 0.25f) / 4f);
                }
            }
        }
        return TypedActionResult.consume(user.getStackInHand(hand));
    }
}
