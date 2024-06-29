package com.pulsar.soulforge.item.weapons.weapon_wheel;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.item.weapons.MagicRangedItem;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.awt.*;

public class DeterminationCrossbow extends MagicRangedItem {
    public boolean loaded = false;

    public HitResult raycast(PlayerEntity player, Vec3d direction, float distance) {
        Vec3d vec3d = player.getCameraPosVec(1f);
        Vec3d vec3d3 = vec3d.add(direction.x * distance, direction.y * distance, direction.z * distance);
        return player.getWorld().raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
    }

    public TypedActionResult<ItemStack> shoot(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            Vec3d end = user.getRotationVector().multiply(getRange()).add(user.getEyePos());
            HitResult hit = raycast(user, user.getRotationVector(), getRange());
            if (hit.getType() != HitResult.Type.MISS) {
                if (hit.getType() != HitResult.Type.MISS) {
                    if (hit.getPos().distanceTo(user.getEyePos()) < getRange()) end = hit.getPos();
                }
            }
            Vec3d start = Utils.getArmPosition(user);
            BlastEntity blast = new BlastEntity(world, start, user, 1f, Vec3d.ZERO, end.subtract(start), 8, Color.RED);
            blast.setPosition(user.getEyePos());
            world.spawnEntity(blast);
            world.playSoundFromEntity(null, user, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 1f, 1f);
            return TypedActionResult.consume(user.getStackInHand(hand));
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    public Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    public TypedActionResult<ItemStack> shootSpread(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            for (int i = 0; i < 3; i++) {
                Vec3d rotationVector = getRotationVector(user.getPitch(), user.getYaw()+30*(i-1));
                Vec3d end = rotationVector.multiply(getRange()).add(user.getEyePos());
                HitResult hit = raycast(user, rotationVector, getRange());
                if (hit.getType() != HitResult.Type.MISS) {
                    if (hit.getPos().distanceTo(user.getEyePos()) < getRange()) end = hit.getPos();
                }
                Vec3d start = Utils.getArmPosition(user);
                BlastEntity blast = new BlastEntity(world, start, user, 0.25f, Vec3d.ZERO, end.subtract(start), 8, Color.RED);
                blast.setPosition(user.getEyePos());
                world.spawnEntity(blast);
            }
            world.playSoundFromEntity(null, user, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 1f, 1f);
            return TypedActionResult.consume(user.getStackInHand(hand));
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!world.isClient) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
            if (loaded) {
                loaded = false;
                if (!user.isSneaking()) return shoot(world, user, hand);
                if (user.isSneaking() && playerSoul.getMagic() >= 25) {
                    playerSoul.setMagic(playerSoul.getMagic()-25f);
                    return shootSpread(world, user, hand);
                }
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
        if (f >= 1.0F && !loaded) {
            loaded = true;
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
        return 20;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW;
    }

    @Override
    public int getRange() {
        return 32;
    }
}
