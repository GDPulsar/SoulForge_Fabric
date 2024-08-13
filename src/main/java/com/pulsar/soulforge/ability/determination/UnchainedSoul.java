package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class UnchainedSoul extends ToggleableAbilityBase {
    Map<StatusEffect, Pair<StatusEffectInstance, Float>> storedEffects = new HashMap<>();

    @Override
    public boolean cast(ServerPlayerEntity player) {
        super.cast(player);
        if (getActive()) {
            storedEffects = new HashMap<>();
            for (Map.Entry<StatusEffect, StatusEffectInstance> effect : Set.copyOf(player.getActiveStatusEffects().entrySet())) {
                if (effect.getKey().isBeneficial()) {
                    storedEffects.put(effect.getKey(), new Pair<>(effect.getValue(), (float)effect.getValue().getDuration()));
                    player.removeStatusEffect(effect.getKey());
                }
            }
        }
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        for (Map.Entry<StatusEffect, StatusEffectInstance> effect : Set.copyOf(player.getActiveStatusEffects().entrySet())) {
            if (effect.getKey().isBeneficial()) {
                storedEffects.put(effect.getKey(), new Pair<>(effect.getValue(), (float)effect.getValue().getDuration()));
                player.removeStatusEffect(effect.getKey());
            }
        }
        for (Map.Entry<StatusEffect, Pair<StatusEffectInstance, Float>> effect : storedEffects.entrySet()) {
            float durationDecimal = effect.getValue().getRight();
            if (player.getAttributeValue(SoulForgeAttributes.EFFECT_DURATION_MULTIPLIER) != 1) {
                durationDecimal -= (float)player.getAttributeValue(SoulForgeAttributes.EFFECT_DURATION_MULTIPLIER);
            } else {
                durationDecimal -= 1f;
            }
            effect.getValue().setRight(durationDecimal);
        }
        for (Map.Entry<StatusEffect, Pair<StatusEffectInstance, Float>> effect : Set.copyOf(storedEffects.entrySet())) {
            if (effect.getValue().getRight() <= 0f) storedEffects.remove(effect.getKey());
        }
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_POWER, "unchained_soul_power");
        Objects.requireNonNull(player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER)).addPersistentModifier(
                new EntityAttributeModifier("unchained_soul_power", 0.05f * storedEffects.size(), EntityAttributeModifier.Operation.ADDITION));
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_COST, "unchained_soul_cost");
        Objects.requireNonNull(player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST)).addPersistentModifier(
                new EntityAttributeModifier("unchained_soul_cost", 0.25f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        return !getActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_POWER, "unchained_soul_power");
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_COST, "unchained_soul_cost");
        for (Map.Entry<StatusEffect, Pair<StatusEffectInstance, Float>> effect : storedEffects.entrySet()) {
            float durationDecimal = effect.getValue().getRight();
            player.addStatusEffect(new StatusEffectInstance(effect.getKey(), (int)durationDecimal, effect.getValue().getLeft().getAmplifier()));
        }
        setActive(false);
        return true;
    }

    public int getLV() { return 17; }

    public int getCost() { return 0; }

    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() { return new UnchainedSoul(); }
}
