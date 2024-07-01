package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FreezeRing extends MagicItem {
    private int iceshockCooldown = 0;
    private int sleepMistCooldown = 0;

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if (!user.isSneaking()) {
                if (iceshockCooldown == 0) {
                    EntityHitResult hit = Utils.getFocussedEntity(user, 10);
                    if (hit != null) {
                        if (hit.getEntity() instanceof LivingEntity target) {
                            if (target instanceof PlayerEntity targetPlayer) {
                                if (!TeamUtils.canDamagePlayer(user.getServer(), user, targetPlayer)) return TypedActionResult.pass(user.getStackInHand(hand));
                            }
                            ServerPlayerEntity serverPlayer = (ServerPlayerEntity)user;
                            serverPlayer.getServerWorld().spawnParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getY(), target.getZ(), 20, 0.5, 1, 0.5, 0.1);
                            serverPlayer.getServerWorld().playSoundFromEntity(null, user, SoulForgeSounds.DR_ICESHOCK_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                            DamageSource damageSource = SoulForgeDamageTypes.of(user.getWorld(), SoulForgeDamageTypes.ABILITY_PIERCE_DAMAGE_TYPE);
                            target.damage(damageSource, 5f);
                            iceshockCooldown = 100;
                            return TypedActionResult.success(user.getStackInHand(hand));
                        }
                    }
                }
            } else {
                if (sleepMistCooldown == 0) {
                    Box searchBox = user.getBoundingBox().expand(10f);
                    HitResult hit = ProjectileUtil.raycast(user, user.getEyePos(), user.getEyePos().add(user.getRotationVector().multiply(10f)), searchBox, entity -> true, 0);
                    if (hit == null || hit.getType() == HitResult.Type.MISS) hit = user.raycast(10f, 0f, false);
                    if (hit != null && hit.getType() != HitResult.Type.MISS) {
                        SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
                        Vec3d pos = hit.getPos();
                        for (Entity target : user.getEntityWorld().getOtherEntities(user, new Box(pos.subtract(3, 3, 3), pos.add(3, 3, 3)))) {
                            if (target instanceof LivingEntity living) {
                                if (living instanceof PlayerEntity targetPlayer) {
                                    if (!TeamUtils.canDamagePlayer(user.getServer(), user, targetPlayer)) continue;
                                }
                                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 30*playerSoul.getEffectiveLV(), (int)(playerSoul.getEffectiveLV()/5f) - 1));
                                living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 30*playerSoul.getEffectiveLV(), Math.max((int)(playerSoul.getEffectiveLV()/5f) - 1, 1)));
                            }
                        }
                        sleepMistCooldown = 600;
                        return TypedActionResult.success(user.getStackInHand(hand));
                    }
                }
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.round(13.0f - (200f-iceshockCooldown) * 13.0f / 200f);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return MathHelper.packRgb(0.4f, 0.4f, 1.0f);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient) {
            if (iceshockCooldown > 0) iceshockCooldown--;
            if (sleepMistCooldown > 0) sleepMistCooldown--;
        }
    }
}
