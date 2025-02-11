package com.pulsar.soulforge.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.tag.SoulForgeTags;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import oshi.util.tuples.Pair;

import java.util.HashMap;

public class LivingDeathEvent {
    private static final HashMap<PlayerEntity, HashMap<DamageType, Pair<Integer, Integer>>> braveryEssenceStuff = new HashMap<>();
    public static void onDeath(LivingEntity living, DamageSource source) {
        if (living instanceof TameableEntity tameable) {
            if (tameable.getOwner() instanceof PlayerEntity player) {
                if (Utils.hasHate(player)) {
                    Utils.addHate(player, 33f);
                }
            }
        }

        if (living instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            playerSoul.onDeath();
        }

        if (living.getPrimeAdversary() instanceof ServerPlayerEntity serverPlayer) {
            if (!braveryEssenceStuff.containsKey(serverPlayer)) {
                HashMap<DamageType, Pair<Integer, Integer>> map = new HashMap<>();
                map.put(source.getType(), new Pair<>(serverPlayer.age, 1));
                braveryEssenceStuff.put(serverPlayer, map);
            } else {
                HashMap<DamageType, Pair<Integer, Integer>> map = braveryEssenceStuff.get(serverPlayer);
                if (!map.containsKey(source.getType())) {
                    map.put(source.getType(), new Pair<>(serverPlayer.age, 1));
                } else {
                    if (map.get(source.getType()).getA() >= serverPlayer.age - 20) {
                        map.put(source.getType(), new Pair<>(serverPlayer.age, map.get(source.getType()).getB() + 1));
                        if (map.get(source.getType()).getB() >= 2) {
                            living.dropItem(SoulForgeItems.BRAVERY_ESSENCE);
                        }
                    } else {
                        map.put(source.getType(), new Pair<>(serverPlayer.age, 1));
                    }
                }
            }

            if (serverPlayer.distanceTo(living) > 40f && source.isIn(DamageTypeTags.IS_PROJECTILE)) {
                living.dropItem(SoulForgeItems.JUSTICE_ESSENCE);
            }

            if (!living.getStatusEffects().isEmpty()) {
                int positive = 0;
                for (StatusEffect effect : living.getActiveStatusEffects().keySet()) {
                    if (effect.isBeneficial()) positive++;
                }
                if (positive >= 2) {
                    living.dropItem(SoulForgeItems.KINDNESS_ESSENCE);
                }
            }

            if (source.isIn(SoulForgeTags.MAGIC_DAMAGE)) {
                living.dropItem(SoulForgeItems.PATIENCE_ESSENCE);
            }

            if (source.isIn(DamageTypeTags.IS_FALL) || source.isOf(DamageTypes.FLY_INTO_WALL)) {
                living.dropItem(SoulForgeItems.INTEGRITY_ESSENCE);
            }

            if (living.lastDamageTaken >= 15f) {
                living.dropItem(SoulForgeItems.PERSEVERANCE_ESSENCE);
            }
        }
    }

    public static void onKilledBy(LivingEntity living, LivingEntity killer) {
        if (killer instanceof ServerPlayerEntity player) {
            SoulComponent soulData = SoulForge.getPlayerSoul(player);
            soulData.setEXP(soulData.getEXP() + Utils.getKillExp(living, player));
            if (living.isMobOrPlayer()) {
                if (living.isPlayer()) soulData.addPlayerSoul(living.getUuidAsString(), 1);
                else soulData.addMonsterSoul(living, 1);
            }

            if (Utils.hasHate(player)) {
                Utils.addHate(player, 1f);
            }
        }
    }
}
