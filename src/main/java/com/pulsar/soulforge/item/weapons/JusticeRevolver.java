package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.justice.JusticePellets;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
import com.pulsar.soulforge.item.special.CrushingRound;
import com.pulsar.soulforge.item.special.FrostbiteRound;
import com.pulsar.soulforge.item.special.PuncturingRound;
import com.pulsar.soulforge.item.special.SuppressingRound;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class JusticeRevolver extends MagicRangedItem {
    public int ammo;

    public JusticeRevolver() {
        ammo = 6;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            NbtCompound stackNbt = stack.getOrCreateNbt();
            if (stackNbt.contains("reloadedCount")) {
                ammo = Math.max(stackNbt.getInt("reloadedCount"), ammo);
            }
            if (ammo > 0) {
                JusticePelletProjectile projectile = new JusticePelletProjectile(world, user);
                if (stackNbt.contains("reloaded") && stackNbt.contains("reloadedCount")) {
                    if (stackNbt.getInt("reloadedCount") > 0) {
                        projectile = switch (stack.getOrCreateNbt().getString("reloaded")) {
                            case "frostbite" -> FrostbiteRound.createPellet(world, user, 1f/5f);
                            case "crushing" -> CrushingRound.createPellet(world, user, 1f/5f);
                            case "puncturing" -> PuncturingRound.createPellet(world, user, 1f/5f);
                            case "suppressing" -> SuppressingRound.createPellet(world, user, 1f/5f);
                            default -> projectile;
                        };
                        stackNbt.putInt("reloadedCount", stackNbt.getInt("reloadedCount")-1);
                        if (stackNbt.getInt("reloadedCount") <= 0) {
                            stackNbt.putBoolean("reloaded", false);
                        }
                    }
                }
                projectile.setPos(user.getEyePos());
                projectile.setVelocity(user.getRotationVector().multiply(8));
                world.spawnEntity(projectile);
                world.playSoundFromEntity(null, user, SoulForgeSounds.GUN_SHOOT_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                ammo--;
                user.getItemCooldownManager().set(this, 10);
                world.playSoundFromEntity(null, user, SoulForgeSounds.GUN_SHOOT_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                return TypedActionResult.consume(user.getStackInHand(hand));
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public int getRange() {
        return 16;
    }
}
