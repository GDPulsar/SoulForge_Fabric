package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FrostWave extends MagicItem {
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
            boolean frostburn = playerSoul.getTraits().contains(Traits.bravery) && playerSoul.getTraits().contains(Traits.patience);
            for (LivingEntity entity : Utils.getEntitiesInFrontOf(user, 3f, 7f, 1f, 2f)) {
                if (entity instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canDamagePlayer(user.getServer(), user, targetPlayer)) continue;
                }
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80 + playerSoul.getEffectiveLV()*40, Math.round(playerSoul.getEffectiveLV()/5f) - 1));
                entity.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY,  80 + playerSoul.getEffectiveLV()*40, Math.min(Math.round(playerSoul.getEffectiveLV()/6f) - 1, 2)));
                entity.damage(SoulForgeDamageTypes.of(user, SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), 1f);
                int duration = 10;
                if (entity.hasStatusEffect(SoulForgeEffects.FROSTBITE)) duration = entity.getStatusEffect(SoulForgeEffects.FROSTBITE).getDuration() + 10;
                if (entity.hasStatusEffect(SoulForgeEffects.FROSTBURN)) duration = entity.getStatusEffect(SoulForgeEffects.FROSTBURN).getDuration() + 10;
                entity.addStatusEffect(new StatusEffectInstance(
                        frostburn ? SoulForgeEffects.FROSTBURN : SoulForgeEffects.FROSTBITE,
                        duration, 0));
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
        return TypedActionResult.consume(user.getStackInHand(hand));
    }
}
