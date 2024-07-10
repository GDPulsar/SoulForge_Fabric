package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
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
                EntityHitResult hit = Utils.getFocussedEntity(user, getRange());
                if (hit != null) {
                    if (hit.getEntity() instanceof LivingEntity target) {
                        SoulComponent playerSoul = SoulForge.getPlayerSoul(user);
                        float damage = 5f + playerSoul.getLV()/2.5f;
                        if (stackNbt.contains("reloaded") && stackNbt.contains("reloadedCount")) {
                            if (stackNbt.getInt("reloadedCount") > 0) {
                                switch (stack.getOrCreateNbt().getString("reloaded")) {
                                    case "frostbite" -> {
                                        Utils.addEffectDuration(target, StatusEffects.SLOWNESS, (int) (900*0.2f), 2);
                                        Utils.addEffectDuration(target, StatusEffects.WEAKNESS, (int) (900*0.2f), 1);
                                        Utils.addEffectDuration(target, SoulForgeEffects.FROSTBITE, (int) (900*0.2f), 1);
                                    }
                                    case "crushing" -> {
                                        Utils.addEffectDuration(target, SoulForgeEffects.VULNERABILITY, (int) (400*0.2f), 2);
                                        Utils.addEffectDuration(target, SoulForgeEffects.CRUSHED, (int) (400*0.2f), 0);
                                    }
                                    case "puncturing" -> {
                                        damage *= 1.5f;
                                    }
                                    case "suppressing" -> {
                                        Utils.addEffectDuration(target, StatusEffects.SLOWNESS, (int)(80*0.2f), 4);
                                        Utils.addEffectDuration(target, StatusEffects.WEAKNESS, (int)(80*0.2f), 3);
                                        Utils.addEffectDuration(target, StatusEffects.MINING_FATIGUE, (int)(80*0.2f), 4);
                                        Utils.addEffectDuration(target, SoulForgeEffects.MANA_SICKNESS, (int)(80*0.2f), 4);
                                    }
                                }
                                stackNbt.putInt("reloadedCount", stackNbt.getInt("reloadedCount")-1);
                                if (stackNbt.getInt("reloadedCount") <= 0) {
                                    stackNbt.putBoolean("reloaded", false);
                                }
                            }
                        }
                        target.damage(SoulForgeDamageTypes.of(user, SoulForgeDamageTypes.GUN_SHOT_DAMAGE_TYPE), damage);
                    }
                }
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
