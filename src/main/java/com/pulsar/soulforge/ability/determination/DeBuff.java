package com.pulsar.soulforge.ability.determination;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.util.Constants;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;

import java.util.ArrayList;
import java.util.List;

public class DeBuff extends AbilityBase {
    public final String name = "De-Buff";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "de_buff");
    public final int requiredLv = 12;
    public final int cost = 35;
    public final int cooldown = 500;
    public final AbilityType type = AbilityType.CAST;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult hit = Utils.getFocussedEntity(player, (float) ReachEntityAttributes.getAttackRange(player, 3.0));
        if (hit != null && hit.getEntity() instanceof LivingEntity living) {
            living.damage(player.getDamageSources().playerAttack(player), 4f);
            List<StatusEffectInstance> newEffects = new ArrayList<>();
            for (StatusEffectInstance instance : living.getStatusEffects()) {
                int highest = 0;
                int duration = instance.getEffectType().isBeneficial() ? instance.getDuration() : instance.getDuration() / 2;
                if (player.hasStatusEffect(instance.getEffectType())) {
                    highest = player.getStatusEffect(instance.getEffectType()).getAmplifier() + 1;
                    duration = Math.max(duration, player.getStatusEffect(instance.getEffectType()).getDuration());
                }
                player.addStatusEffect(new StatusEffectInstance(instance.getEffectType(), duration, Math.min(Constants.effectHighest.get(instance.getEffectType()), highest)));
                if (instance.getAmplifier() > 0) {
                    newEffects.add(new StatusEffectInstance(instance.getEffectType(), instance.getDuration(), instance.getAmplifier() - 1));
                }
            }
            living.clearStatusEffects();
            for (StatusEffectInstance instance : newEffects) {
                living.addStatusEffect(instance);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        return true;
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
        return new DeBuff();
    }
}
