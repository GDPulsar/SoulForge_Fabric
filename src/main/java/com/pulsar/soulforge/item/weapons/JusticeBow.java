package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.JusticeArrowProjectile;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JusticeBow extends MagicRangedItem {
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(playerEntity);

            int i = this.getMaxUseTime(stack) - remainingUseTicks;
            float f = getPullProgress(i);
            if (!((double)f < 0.1D)) {
                if (!world.isClient) {
                    JusticeArrowProjectile arrow = new JusticeArrowProjectile(world, playerEntity);
                    arrow.setPosition(playerEntity.getEyePos());
                    arrow.setVelocity(user.getRotationVector().x, user.getRotationVector().y, user.getRotationVector().z, f * 3f, 1f);
                    arrow.setDamage(playerSoul.getLV() * 0.75f * f);
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
        return 15;
    }

    public void scatter(PlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getMagic() >= 10f) {
            for (JusticeArrowProjectile arrow : player.getWorld().getEntitiesByClass(JusticeArrowProjectile.class, Box.of(player.getPos(), 200, 200, 200), entity -> entity.getOwner() == player)) {
                for (int i = 0; i < (4+playerSoul.getLV()/4); i++) {
                    JusticePelletProjectile pellet = new JusticePelletProjectile(player.getWorld(), player);
                    pellet.setPos(new Vec3d(arrow.getX(), arrow.getY(), arrow.getZ()));
                    Vec3d pelletVel = arrow.getVelocity().normalize().multiply(2f)
                            .add(new Vec3d(Math.random() - 0.5f, Math.random() - 0.5f,Math.random() - 0.5f)).normalize().multiply(4f);
                    pellet.setVelocity(pelletVel);
                    player.getWorld().spawnEntity(pellet);
                }
                playerSoul.setMagic(playerSoul.getMagic()-10f);
                if (playerSoul.getMagic() < 10f) break;
            }
        }
    }

    public void aim(PlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getMagic() >= 3f) {
            List<ProjectileEntity> projectiles = new ArrayList<>();
            projectiles.addAll(player.getWorld().getEntitiesByClass(JusticeArrowProjectile.class, Box.of(player.getPos(), 200, 200, 200), entity -> entity.getOwner() == player));
            projectiles.addAll(player.getWorld().getEntitiesByClass(JusticePelletProjectile.class, Box.of(player.getPos(), 200, 200, 200), entity -> entity.getOwner() == player));
            Collections.shuffle(projectiles);
            for (ProjectileEntity projectile : projectiles) {
                projectile.setVelocity(player.getRotationVector().multiply(projectile.getVelocity().length()));
                projectile.velocityModified = true;
                playerSoul.setMagic(playerSoul.getMagic() - 3f);
                if (playerSoul.getMagic() < 3f) break;
            }
        }
    }
}
