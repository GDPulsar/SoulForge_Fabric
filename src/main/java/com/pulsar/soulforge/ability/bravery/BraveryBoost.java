package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

public class BraveryBoost extends ToggleableAbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (!getActive()) {
            if (playerSoul.getMagic() < 100f) {
                setActive(false);
                return false;
            }
            playerSoul.setMagic(0f);
        }
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "bravery_boost_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "bravery_boost_strength");
        EntityAttributeModifier healthModifier = new EntityAttributeModifier("bravery_boost_health", playerSoul.getEffectiveLV() / 40f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        EntityAttributeModifier strengthModifier = new EntityAttributeModifier("bravery_boost_strength", playerSoul.getEffectiveLV() / 40f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(healthModifier);
        player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(strengthModifier);
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "bravery_boost_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "bravery_boost_strength");
        return super.end(player);
    }


    public int getLV() { return 15; }

    public int getCost() { return 100; }

    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new BraveryBoost();
    }
}
