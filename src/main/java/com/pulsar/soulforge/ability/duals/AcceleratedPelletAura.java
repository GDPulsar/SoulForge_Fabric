package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

public class AcceleratedPelletAura extends ToggleableAbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        super.cast(player);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.removeTag("fallImmune");
        playerSoul.removeValue("jumpBoost");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "apa_speed");
        if (getActive()) {
            playerSoul.addTag("fallImmune");
            playerSoul.setValue("jumpBoost", 2f);
            player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier("apa_speed", playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        return getActive();
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "apa_speed");
        player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier("apa_speed", playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        if (!playerSoul.hasCast("Warpspeed")) player.setStepHeight(1.6f);
        playerSoul.addTag("fallImmune");
        playerSoul.setValue("jumpBoost", 2f);
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.removeTag("fallImmune");
        playerSoul.removeValue("jumpBoost");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "apa_speed");
        return super.end(player);
    }

    public int getLV() { return 15; }

    public int getCost() { return 100; }

    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new AcceleratedPelletAura();
    }
}
