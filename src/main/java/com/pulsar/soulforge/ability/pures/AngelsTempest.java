package com.pulsar.soulforge.ability.pures;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.patience.BlindingSnowstorm;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AngelsTempest extends AbilityBase{
    public final String name = "Angel's Tempest";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "angels_tempest");
    public final int requiredLv = 12;
    public final int cost = 200;
    public final int cooldown = 4800;
    public final AbilityType type = AbilityType.CAST;

    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        timer = 300;
        boolean inSnowstorm = false;
        BlindingSnowstorm snowstorm = null;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        for (AbilityBase ability : playerSoul.getActiveAbilities()) {
            if (ability instanceof BlindingSnowstorm) {
                snowstorm = (BlindingSnowstorm)ability;
                if (snowstorm.location.toCenterPos().squaredDistanceTo(player.getPos()) < snowstorm.size * snowstorm.size) {
                    inSnowstorm = true;
                }
            }
        }
        if (inSnowstorm) {
            HashMap<StatusEffect, StatusEffectInstance> effects = new HashMap<>();
            List<LivingEntity> targets = new ArrayList<>();
            for (Entity entity : player.getEntityWorld().getOtherEntities(player, Box.of(snowstorm.location.toCenterPos(), snowstorm.size*2, snowstorm.size*2, snowstorm.size*2))) {
                if (entity instanceof LivingEntity target) {
                    if (target instanceof PlayerEntity targetPlayer) {
                        if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
                    }
                    if (target.squaredDistanceTo(snowstorm.location.toCenterPos()) <= snowstorm.size * snowstorm.size) {
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
                        if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
                    }
                    for (StatusEffectInstance effect : effects) {
                        target.addStatusEffect(new StatusEffectInstance(effect.getEffectType(), effect.getDuration(), effect.getAmplifier()*2-1));
                    }
                }
            }
        }
        return true;
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

    @Override
    public boolean end(ServerPlayerEntity player) {
        return true;
    }

    public String getName() { return name; }

    public Text getLocalizedText() { return Text.translatable("ability."+id.getPath()+".name"); }

    public Identifier getID() { return id; }

    public String getTooltip() { return Text.translatable("ability."+id.getPath()+".tooltip").getString(); }

    public int getLV() { return requiredLv; }

    public int getCost() { return cost; }

    public int getCooldown() { return cooldown; }

    public AbilityType getType() { return type; }

    @Override
    public AbilityBase getInstance() {
        return new AngelsTempest();
    }
}
