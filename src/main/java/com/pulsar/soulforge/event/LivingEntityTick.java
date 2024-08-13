package com.pulsar.soulforge.event;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.despair.DrainingField;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.ValueComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class LivingEntityTick {
    public static void tick(LivingEntity living) {
        ValueComponent values = SoulForge.getValues(living);
        if (values.hasInt("HangToAThreadTimer")) {
            values.setInt("HangToAThreadTimer", values.getInt("HangToAThreadTimer") - 1);
            if (!values.hasBool("HangToAThreadDamaging") || !values.getBool("HangToAThreadDamaging")) {
                if (values.getInt("HangToAThreadTimer") % 5 == 0) {
                    living.getWorld().playSound(null, living.getBlockPos(), SoulForgeSounds.UT_TICK_EVENT, SoundCategory.MASTER, 1f, 1f);
                }
                if (values.getInt("HangToAThreadTimer") == 0) {
                    values.setBool("HangToAThreadDamaging", true);
                    values.setInt("HangToAThreadTimer", 140);
                    values.setInt("HangToAThreadDamageCount", 0);
                    living.getWorld().playSound(null, living.getBlockPos(), SoulForgeSounds.UT_CHAINSAW_EVENT, SoundCategory.MASTER, 1f, 1f);
                }
            } else {
                if (140 - values.getInt("HangToAThreadTimer") >=
                        values.getInt("HangToAThreadDamageCount") * (140f / values.getFloat("HangToAThreadDamage"))) {
                    living.timeUntilRegen = 0;
                    living.damage(SoulForgeDamageTypes.of(living.getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), Math.max(1f, values.getFloat("HangToAThreadDamage") / 140f));
                    values.setInt("HangToAThreadDamageCount", values.getInt("HangToAThreadDamageCount") + 1);
                }
                if (values.getInt("HangToAThreadTimer") == 0) {
                    values.removeInt("HangToAThreadTimer");
                    values.removeFloat("HangToAThreadDamage");
                    values.removeBool("HangToAThreadDamaging");
                }
            }
        }
        if (values.hasInt("ChildOfOmelasTimer")) {
            values.setInt("ChildOfOmelasTimer", values.getInt("ChildOfOmelasTimer") - 1);
            if (values.getInt("ChildOfOmelasTimer") == 0) {
                values.removeInt("ChildOfOmelasTimer");
            }
        }

        PlayerEntity nearestReaper = null;
        for (PlayerEntity player : living.getWorld().getPlayers()) {
            if (player.distanceTo(living) < 20f) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                if (!playerSoul.hasAbility("Draining Field")) continue;
                DrainingField drainingField = (DrainingField)playerSoul.getAbility("Draining Field");
                if (!drainingField.getActive() && player.distanceTo(living) > 10f) continue;
                if (nearestReaper == null) nearestReaper = player;
                else {
                    if (player.distanceTo(living) < nearestReaper.distanceTo(living)) {
                        nearestReaper = player;
                    }
                }
            }
        }
        if (nearestReaper != null) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(nearestReaper);
            DrainingField drainingField = (DrainingField)playerSoul.getAbility("Draining Field");
            if (drainingField.getActive()) {
                values.removeUUID("DrainingField");
                values.setUUID("ReapingField", nearestReaper.getUuid());
                if (values.hasInt("ReapingFieldTimer")) {
                    values.setInt("ReapingFieldTimer", values.getInt("ReapingFieldTimer") + 1);
                } else {
                    values.setInt("ReapingFieldTimer", 0);
                }
                values.setFloat("ReapingFieldAmount", 0.35f + MathHelper.clamp((values.getInt("ReapingFieldTimer") / 20) * 5f, 0f, 0.6f));
            } else {
                values.removeUUID("ReapingField");
                values.removeFloat("ReapingFieldAmount");
                values.setUUID("DrainingField", nearestReaper.getUuid());
            }
        } else {
            values.removeUUID("DrainingField");
            values.removeUUID("ReapingField");
            values.removeFloat("ReapingFieldAmount");
        }

        if (living.hasStatusEffect(SoulForgeEffects.EEPY)) {
            if (living.getSleepingPosition().isEmpty()) {
                 if (values.getBool("WasEepy")) {
                    living.setInvulnerable(false);
                }
                for (int x = -4; x < 4; x++) {
                    for (int y = -2; y < 2; y++) {
                        for (int z = -4; z < 4; z++) {
                            BlockPos pos = new BlockPos(x, y, z).add(living.getBlockPos());
                            BlockState blockState = living.getWorld().getBlockState(pos);
                            if (blockState.isIn(BlockTags.BEDS)) {
                                if (BedBlock.getBedPart(blockState) != DoubleBlockProperties.Type.FIRST) {
                                    pos = pos.offset(BedBlock.getDirection(living.getWorld(), pos));
                                }
                                living.sleep(pos);
                                living.getWorld().playSound(null, pos, SoulForgeSounds.EEPY_EVENT, SoundCategory.MASTER, 1f, 1f);
                            }
                        }
                    }
                }
            } else if (living.isSleepingInBed()) {
                living.setInvulnerable(true);
                values.setBool("WasEepy", true);
                living.setPositionInBed(living.getSleepingPosition().get());
            }
        } else if (values.getBool("WasEepy")) {
            living.setInvulnerable(false);
        }

        if (living.hasStatusEffect(SoulForgeEffects.MANA_TUMOR)) {
            StatusEffectInstance tumor = living.getStatusEffect(SoulForgeEffects.MANA_TUMOR);
            if (tumor.getDuration() <= 1 && tumor.getAmplifier() < 2) {
                int duration = (int)Math.floor((Math.random() + 1) * 72000) * tumor.getAmplifier() == 0 ? 3 : 1;
                living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_TUMOR, duration, tumor.getAmplifier() + 1));
            } else if (tumor.getDuration() <= 1) {
                if (SoulForge.getValues(living).hasBool("PestilenceTumor") && SoulForge.getValues(living).getBool("PestilenceTumor")) {
                    SoulForge.getValues(living).removeBool("PestilenceTumor");
                } else {
                    living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.TUMOR_IMMUNITY, 144000, 0));
                }
                living.removeStatusEffect(SoulForgeEffects.MANA_TUMOR);
            }

            if (tumor.getAmplifier() == 1) {
                if (Math.random() < 0.0001) {
                    StatusEffect effect = List.of(StatusEffects.WEAKNESS, StatusEffects.BLINDNESS, StatusEffects.NAUSEA,
                            StatusEffects.SLOWNESS, StatusEffects.HUNGER, SoulForgeEffects.FROSTBITE).get((int)Math.floor(Math.random()*6));
                    living.addStatusEffect(new StatusEffectInstance(effect, 160 + (int)(Math.floor(Math.random() * 80)), (int)Math.round(Math.random() * 2)));
                }
            }

            if (tumor.getAmplifier() == 2) {
                if (Math.random() < 0.0005) {
                    StatusEffect effect = List.of(StatusEffects.WEAKNESS, StatusEffects.BLINDNESS, StatusEffects.NAUSEA,
                            StatusEffects.SLOWNESS, StatusEffects.HUNGER, SoulForgeEffects.FROSTBITE).get((int)Math.floor(Math.random()*6));
                    living.addStatusEffect(new StatusEffectInstance(effect, 320 + (int)(Math.floor(Math.random() * 160)), (int)Math.round(Math.random() * 2) + 2));
                }
                if (Math.random() < 0.0003 && !(values.hasBool("PestilenceTumor") && values.getBool("PestilenceTumor"))) {
                    List<LivingEntity> near = living.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(living.getPos(), 12, 12,12),
                            (entity) -> entity.distanceTo(living) < 6f);
                    near.get((int)Math.floor(Math.random()*near.size())).addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_TUMOR, (int)Math.floor((Math.random() + 0.5f) * 36000)));
                }
            }
        }

        if (living instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (playerSoul.hasCast("Proceed")) {
                LivingEntity nearest = null;
                for (LivingEntity nearby : player.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(player.getPos(), 50, 50, 50), entity -> player.canHit())) {
                    if (nearby == player) continue;
                    if (nearest == null) nearest = nearby;
                    else if (nearby.distanceTo(player) < nearest.distanceTo(player)) {
                        nearest = nearby;
                    }
                }
                if (nearest != null) {
                    player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, nearest.getEyePos());
                }
            }
        }
    }
}
