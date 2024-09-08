package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AuraAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class DeterminationAura extends AuraAbilityBase {
    @Override
    public HashMap<EntityAttribute, EntityAttributeModifier> getModifiers(int elv) {
        return new HashMap<>(Map.ofEntries(
                entry(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier("determination_aura_health", elv / 2f, EntityAttributeModifier.Operation.ADDITION)),
                entry(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier("determination_aura_damage", elv * 0.0175f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL))
        ));
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        int healRate = (int)(400f / playerSoul.getEffectiveLV());
        if (player.age % healRate == 0) {
            player.heal(1f);
        }
        return super.tick(player);
    }

    public int getLV() { return 19; }

    public int getCost() { return 100; }

    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new DeterminationAura();
    }
}
