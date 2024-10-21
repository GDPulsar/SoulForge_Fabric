package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
import com.pulsar.soulforge.item.special.CrushingRound;
import com.pulsar.soulforge.item.special.FrostbiteRound;
import com.pulsar.soulforge.item.special.PuncturingRound;
import com.pulsar.soulforge.item.special.SuppressingRound;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class JusticeRevolver extends MagicRangedItem {
    public JusticeRevolver() {}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            NbtCompound stackNbt = stack.getOrCreateNbt();
            int ammo = stackNbt.contains("ammo") ? stackNbt.getInt("ammo") : 0;
            if (stackNbt.contains("reloadedCount")) {
                ammo = Math.max(stackNbt.getInt("reloadedCount"), ammo);
            }
            if (ammo > 0) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
                float damage = 5f + playerSoul.getLV()/2.5f;
                JusticePelletProjectile projectile = new JusticePelletProjectile(world, user, damage);
                if (stackNbt.contains("reloaded") && stackNbt.contains("reloadedCount")) {
                    if (stackNbt.getInt("reloadedCount") > 0) {
                        switch (stack.getOrCreateNbt().getString("reloaded")) {
                            case "frostbite" -> {
                                projectile = FrostbiteRound.createPellet(world, user, damage, 0.2f);
                            }
                            case "crushing" -> {
                                projectile = CrushingRound.createPellet(world, user, damage, 0.2f);
                            }
                            case "puncturing" -> {
                                projectile = PuncturingRound.createPellet(world, user, damage * 1.5f, 0.2f);
                            }
                            case "suppressing" -> {
                                projectile = SuppressingRound.createPellet(world, user, damage, 0.2f);
                            }
                        }
                        stackNbt.putInt("reloadedCount", stackNbt.getInt("reloadedCount")-1);
                        if (stackNbt.getInt("reloadedCount") <= 0) {
                            stackNbt.putBoolean("reloaded", false);
                        }
                    }
                }
                projectile.setPos(user.getEyePos());
                projectile.setVelocity(user.getRotationVector().multiply(8));
                world.spawnEntity(projectile);
                ammo--;
                stackNbt.putInt("ammo", ammo);
                user.getItemCooldownManager().set(this, 10);
                world.playSoundFromEntity(null, user, SoulForgeSounds.GUN_SHOOT_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                return TypedActionResult.consume(user.getStackInHand(hand));
            }
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    public int getRange() {
        return 50;
    }
}
