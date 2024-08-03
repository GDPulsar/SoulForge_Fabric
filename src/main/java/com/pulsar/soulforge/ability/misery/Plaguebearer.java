package com.pulsar.soulforge.ability.misery;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.accessors.ValueHolder;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;

public class Plaguebearer extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult result = Utils.getFocussedEntity(player, (float)ReachEntityAttributes.getReachDistance(player, 3f));
        if (result != null) {
            if (result.getEntity() instanceof LivingEntity living) {
                if (!living.hasStatusEffect(SoulForgeEffects.MANA_TUMOR)) {
                    ((ValueHolder)living).setUUID("TumorOwner", player.getUuid());
                    living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_TUMOR, (int)Math.floor((Math.random() + 0.5f) * 36000)));
                } else {
                    StatusEffectInstance tumor = living.getStatusEffect(SoulForgeEffects.MANA_TUMOR);
                    if (tumor.getAmplifier() < 2) {
                        int duration = (int)Math.floor((Math.random() + 1) * 72000) * tumor.getAmplifier() == 0 ? 3 : 1;
                        living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_TUMOR, duration, tumor.getAmplifier() + 1));
                    } else {
                        if (((ValueHolder)living).hasBool("PestilenceTumor") && ((ValueHolder)living).getBool("PestilenceTumor")) {
                            ((ValueHolder)living).removeBool("PestilenceTumor");
                        } else {
                            living.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.TUMOR_IMMUNITY, 144000, 0));
                        }
                        living.removeStatusEffect(SoulForgeEffects.MANA_TUMOR);
                    }
                }
                return super.cast(player);
            }
        }
        return false;
    }

    public int getLV() { return 20; }

    public int getCost() { return 20; }

    public int getCooldown() { return 1200; }

    public AbilityType getType() { return AbilityType.CAST; }
    @Override
    public AbilityBase getInstance() {
        return new Plaguebearer();
    }
}
