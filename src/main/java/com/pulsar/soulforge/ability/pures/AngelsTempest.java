package com.pulsar.soulforge.ability.pures;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.patience.BlindingSnowstorm;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AngelsTempest extends AbilityBase{
    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        timer = 300;
        boolean inSnowstorm = false;
        BlindingSnowstorm snowstorm = null;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getStyleRank() < 2) {
            player.sendMessageToClient(Text.translatable(Math.random() < 0.01f ? "soulforge.style.get_real" : "soulforge.style.not_enough"), true);
            return false;
        }
        for (AbilityBase ability : playerSoul.getActiveAbilities()) {
            if (ability instanceof BlindingSnowstorm) {
                snowstorm = (BlindingSnowstorm)ability;
                if (snowstorm.location.squaredDistanceTo(player.getPos()) < snowstorm.size * snowstorm.size) {
                    inSnowstorm = true;
                }
            }
        }
        if (inSnowstorm) {
            HashMap<StatusEffect, StatusEffectInstance> effects = new HashMap<>();
            List<LivingEntity> targets = new ArrayList<>();
            for (Entity entity : player.getEntityWorld().getOtherEntities(player, Box.of(snowstorm.location, snowstorm.size*2, snowstorm.size*2, snowstorm.size*2))) {
                if (entity instanceof LivingEntity target) {
                    if (target instanceof PlayerEntity targetPlayer) {
                        if (!TeamUtils.canDamageEntity(player.getServer(), player, targetPlayer)) continue;
                    }
                    if (target.squaredDistanceTo(snowstorm.location) <= snowstorm.size * snowstorm.size) {
                        targets.add(target);
                        List<StatusEffectInstance> targetEffects = new ArrayList<>();
                        for (StatusEffectInstance effect : target.getStatusEffects()) {
                            if (!effect.getEffectType().isBeneficial()) {
                                if (effects.containsKey(effect.getEffectType())) {
                                    if (effect.getAmplifier() > effects.get(effect.getEffectType()).getAmplifier()) {
                                        effects.put(effect.getEffectType(), effect);
                                    } else if (effect.getAmplifier() == effects.get(effect.getEffectType()).getAmplifier()) {
                                        if (effect.getDuration() >= effects.get(effect.getEffectType()).getDuration()) {
                                            effects.put(effect.getEffectType(), effect);
                                        }
                                    }
                                } else {
                                    effects.put(effect.getEffectType(), effect);
                                }
                            } else {
                                targetEffects.add(effect);
                            }
                        }
                        target.clearStatusEffects();
                        for (StatusEffectInstance effect : targetEffects) {
                            target.addStatusEffect(new StatusEffectInstance(effect.getEffectType(), effect.getDuration(), effect.getAmplifier()-1));
                        }
                    }
                }
            }
            for (LivingEntity target : targets) {
                for (StatusEffectInstance effect : effects.values()) {
                    StatusEffectInstance instance = new StatusEffectInstance(effect.getEffectType(), effect.getDuration(), effect.getAmplifier()*2-1);
                    target.addStatusEffect(instance);
                }
            }
        } else {
            List<StatusEffectInstance> effects = new ArrayList<>();
            for (StatusEffectInstance effect : player.getStatusEffects()) {
                if (!effect.getEffectType().isBeneficial()) {
                    effects.add(effect);
                }
            }
            for (Entity entity : player.getEntityWorld().getOtherEntities(player, Box.of(player.getPos(), 10, 10, 10))) {
                if (entity instanceof LivingEntity target) {
                    if (target instanceof PlayerEntity targetPlayer) {
                        if (!TeamUtils.canDamageEntity(player.getServer(), player, targetPlayer)) continue;
                    }
                    for (StatusEffectInstance effect : effects) {
                        target.addStatusEffect(new StatusEffectInstance(effect.getEffectType(), effect.getDuration(), effect.getAmplifier()*2-1));
                    }
                }
            }
        }
        playerSoul.setStyleRank(playerSoul.getStyleRank() - 2);
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer--;
        List<StatusEffectInstance> newEffects = new ArrayList<>();
        for (StatusEffectInstance effect : player.getStatusEffects()) {
            if (effect.getEffectType().isBeneficial()) {
                newEffects.add(effect);
            }
        }
        player.clearStatusEffects();
        for (StatusEffectInstance effect : newEffects) {
            player.addStatusEffect(new StatusEffectInstance(effect.getEffectType(), effect.getDuration(), effect.getAmplifier()-1));
        }
        return timer <= 0;
    }

    public String getName() { return "Angel's Tempest"; }

    public Identifier getID() { return new Identifier(SoulForge.MOD_ID, "angels_tempest"); }

    public int getLV() { return 12; }

    public int getCost() { return 200; }

    public int getCooldown() { return 4800; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new AngelsTempest();
    }
}
