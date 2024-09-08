package com.pulsar.soulforge.ability;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public abstract class AuraAbilityBase extends ToggleableAbilityBase {
    public HashMap<EntityAttribute, EntityAttributeModifier> getModifiers(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        return getModifiers(playerSoul.getEffectiveLV());
    }
    public abstract HashMap<EntityAttribute, EntityAttributeModifier> getModifiers(int elv);

    public boolean hasFallImmunity() {
        return false;
    }

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (!getActive()) {
            if (playerSoul.getMagic() < 100f) return false;
            playerSoul.setMagic(0f);
        }
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> modifier : getModifiers(player).entrySet()) {
            Utils.clearModifiersByName(player, modifier.getKey(), modifier.getValue().getName());
            player.getAttributeInstance(modifier.getKey()).addPersistentModifier(modifier.getValue());
        }
        if (hasFallImmunity()) player.fallDistance = -1f;
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> modifier : getModifiers(player).entrySet()) {
            Utils.clearModifiersByName(player, modifier.getKey(), modifier.getValue().getName());
        }
        return super.end(player);
    }

    @Override
    public AbilityType getType() {
        return AbilityType.AURA;
    }
}
