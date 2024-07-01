package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AcceleratedPelletAura extends ToggleableAbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        super.cast(player);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.removeTag("fallImmune");
        playerSoul.removeValue("jumpBoost");
        if (isActive()) {
            playerSoul.addTag("fallImmune");
            playerSoul.setValue("jumpBoost", 2f);
        }
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(Identifier.of(SoulForge.MOD_ID, "apa_speed"));
        player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "apa_speed"), playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).removeModifier(Identifier.of(SoulForge.MOD_ID, "apa_step_height"));
        player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).addPersistentModifier(new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "apa_step_height"), 1f, EntityAttributeModifier.Operation.ADD_VALUE));
        playerSoul.addTag("fallImmune");
        playerSoul.setValue("jumpBoost", 2f);
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.removeTag("fallImmune");
        playerSoul.removeValue("jumpBoost");
        player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(Identifier.of(SoulForge.MOD_ID, "apa_speed"));
        player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).removeModifier(Identifier.of(SoulForge.MOD_ID, "apa_step_height"));
        return true;
    }

    public int getLV() { return 15; }
    public int getCost() { return 100; }
    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new AcceleratedPelletAura();
    }
}
