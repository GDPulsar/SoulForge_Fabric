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
        if (getActive()) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            playerSoul.setMagic(0f);
        }
        return getActive();
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "perseverance_aura_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ARMOR, "perseverance_aura_armor");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "perseverance_aura_strength");
        EntityAttributeModifier healthModifier = new EntityAttributeModifier("perseverance_aura_health", playerSoul.getEffectiveLV() / 2f, EntityAttributeModifier.Operation.ADDITION);
        EntityAttributeModifier armorModifier = new EntityAttributeModifier("perseverance_aura_armor", playerSoul.getEffectiveLV() / 1.66f, EntityAttributeModifier.Operation.ADDITION);
        EntityAttributeModifier strengthModifier = new EntityAttributeModifier("perseverance_aura_strength", playerSoul.getEffectiveLV() * 0.0175f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(healthModifier);
        player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addPersistentModifier(armorModifier);
        player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(strengthModifier);
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "perseverance_aura_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ARMOR, "perseverance_aura_armor");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "perseverance_aura_strength");
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
