package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.Constants;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;

import java.util.ArrayList;
import java.util.List;

public class DeBuff extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult hit = Utils.getFocussedEntity(player, 5f);
        if (hit != null && hit.getEntity() instanceof LivingEntity living) {
            int stolenEffectCount = 0;
            int stolenDurationCount = 0;
            living.damage(SoulForgeDamageTypes.of(player, SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), 4f);
            List<StatusEffectInstance> newEffects = new ArrayList<>();
            for (StatusEffectInstance instance : living.getStatusEffects()) {
                int highest = 0;
                int duration = instance.getEffectType().isBeneficial() ? instance.getDuration() : instance.getDuration() / 2;
                stolenEffectCount++;
                stolenDurationCount += duration;
                if (player.hasStatusEffect(instance.getEffectType())) {
                    highest = player.getStatusEffect(instance.getEffectType()).getAmplifier() + 1;
                    duration = Math.max(duration, player.getStatusEffect(instance.getEffectType()).getDuration());
                }
                player.addStatusEffect(new StatusEffectInstance(instance.getEffectType(), duration, Math.min(
                        Constants.effectHighest.getOrDefault(instance.getEffectType(), 3), highest)));
                if (instance.getAmplifier() > 0) {
                    newEffects.add(new StatusEffectInstance(instance.getEffectType(), instance.getDuration(), instance.getAmplifier() - 1));
                }
            }
            living.clearStatusEffects();
            for (StatusEffectInstance instance : newEffects) {
                living.addStatusEffect(instance);
            }
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            playerSoul.setStyle(playerSoul.getStyle() + (int)(stolenEffectCount * stolenDurationCount / 1200f));
            return super.cast(player);
        }
        return false;
    }
    
    public String getName() { return "De-Buff"; }

    public Identifier getID() { return new Identifier(SoulForge.MOD_ID, "de_buff"); }

    public int getLV() { return 12; }

    public int getCost() { return 35; }

    public int getCooldown() { return 500; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new DeBuff();
    }
}
