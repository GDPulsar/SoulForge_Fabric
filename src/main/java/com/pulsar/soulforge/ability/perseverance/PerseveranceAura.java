package com.pulsar.soulforge.ability.perseverance;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

public class PerseveranceAura extends ToggleableAbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        super.cast(player);
        if (isActive()) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            playerSoul.setMagic(0f);
        }
        return isActive();
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        Utils.removeModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, "perseverance_aura_health");
        Utils.removeModifier(player, EntityAttributes.GENERIC_ARMOR, "perseverance_aura_armor");
        Utils.removeModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "perseverance_aura_strength");
        Utils.addModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, "perseverance_aura_health", playerSoul.getEffectiveLV() / 2f, EntityAttributeModifier.Operation.ADD_VALUE);
        Utils.addModifier(player, EntityAttributes.GENERIC_ARMOR, "perseverance_aura_armor", playerSoul.getEffectiveLV() / 1.66f, EntityAttributeModifier.Operation.ADD_VALUE);
        Utils.addModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "perseverance_aura_strength", playerSoul.getEffectiveLV() * 0.0175f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Utils.removeModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, "perseverance_aura_health");
        Utils.removeModifier(player, EntityAttributes.GENERIC_ARMOR, "perseverance_aura_armor");
        Utils.removeModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "perseverance_aura_strength");
        return super.end(player);
    }

    public int getLV() { return 15; }
    public int getCost() { return 100; }
    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new PerseveranceAura();
    }
}
