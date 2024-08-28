package com.pulsar.soulforge.item.weapons.weapon_wheel;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.item.weapons.MagicRangedItem;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DeterminationCrossbow extends MagicRangedItem {
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
            if (itemStack.getOrCreateNbt().contains("loaded") && itemStack.getOrCreateNbt().getBoolean("loaded")) {
                itemStack.getOrCreateNbt().putBoolean("loaded", false);
                HitResult hit = ProjectileUtil.getCollision(user, (entity) ->
                                entity instanceof LivingEntity living && TeamUtils.canDamageEntity(user.getServer(), user, living),
                        300f);
                if (hit instanceof EntityHitResult && ((EntityHitResult)hit).getEntity() instanceof LivingEntity living) {
                    living.damage(SoulForgeDamageTypes.of(user, world, SoulForgeDamageTypes.SNIPED_DAMAGE_TYPE), 1.5f * playerSoul.getEffectiveLV());
                }
                if (hit != null) {
                    double distance = user.getEyePos().distanceTo(hit.getPos());
                    int particleCount = (int) (distance * 10);
                    ServerWorld serverWorld = (ServerWorld) world;
                    for (int i = 0; i < particleCount; i++) {
                        Vec3d pos = user.getEyePos().lerp(hit.getPos(), i / (float) particleCount);
                        serverWorld.spawnParticles(new DustParticleEffect(Vec3d.unpackRgb(0xFF0000).toVector3f(), 0.5f), pos.x, pos.y, pos.z, 1, 0f, 0f, 0f, 0f);
                    }
                }
                world.playSound(null, user.getBlockPos(), SoulForgeSounds.DR_LOWER_HEAVY_EVENT, SoundCategory.PLAYERS, 1f, 1f);
            } else if (playerSoul.getMagic() >= 5) {
                user.setCurrentHand(hand);
                return TypedActionResult.consume(itemStack);
            }
        }
        return TypedActionResult.fail(itemStack);
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        float f = getPullProgress(i, stack);
        if (f >= 1.0F && !(stack.getOrCreateNbt().contains("loaded") && stack.getOrCreateNbt().getBoolean("loaded"))) {
            stack.getOrCreateNbt().putBoolean("loaded", true);
            SoundCategory soundCategory = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, soundCategory, 1.0F, 1.0F);
        }
    }

    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.isOf(this);
    }

    private static float getPullProgress(int useTicks, ItemStack stack) {
        float f = (float)useTicks / (float)getPullTime(stack);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    public int getMaxUseTime(ItemStack stack) {
        return getPullTime(stack) + 3;
    }

    public static int getPullTime(ItemStack stack) {
        return 100;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW;
    }

    @Override
    public int getRange() {
        return 300;
    }
}
