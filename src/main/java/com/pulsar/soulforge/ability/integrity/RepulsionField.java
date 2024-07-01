package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

public class RepulsionField extends ToggleableAbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        super.cast(player);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.removeTag("fallImmune");
        playerSoul.removeValue("jumpBoost");
        Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "repulsion_field_speed");
        Utils.removeModifier(player, SoulForgeAttributes.AIR_SPEED, "repulsion_field_air_speed");
        Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "repulsion_field_step_height");
        if (isActive()) {
            playerSoul.addTag("fallImmune");
            playerSoul.setValue("jumpBoost", 2f);
            Utils.addModifier(player,  EntityAttributes.GENERIC_MOVEMENT_SPEED, "repulsion_field_speed", playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            Utils.addModifier(player,  SoulForgeAttributes.AIR_SPEED, "repulsion_field_air_speed", playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            Utils.addModifier(player,  EntityAttributes.GENERIC_STEP_HEIGHT, "repulsion_field_step_height", 1f, EntityAttributeModifier.Operation.ADD_VALUE);
        }
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "repulsion_field_speed");
        Utils.removeModifier(player, SoulForgeAttributes.AIR_SPEED, "repulsion_field_air_speed");
        Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "repulsion_field_step_height");
        Utils.addModifier(player,  EntityAttributes.GENERIC_MOVEMENT_SPEED, "repulsion_field_speed", playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        Utils.addModifier(player,  SoulForgeAttributes.AIR_SPEED, "repulsion_field_air_speed", playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        Utils.addModifier(player,  EntityAttributes.GENERIC_STEP_HEIGHT, "repulsion_field_step_height", 1f, EntityAttributeModifier.Operation.ADD_VALUE);
        playerSoul.addTag("fallImmune");
        playerSoul.setValue("jumpBoost", 2f);
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.removeTag("fallImmune");
        playerSoul.removeValue("jumpBoost");
        Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "repulsion_field_speed");
        Utils.removeModifier(player, SoulForgeAttributes.AIR_SPEED, "repulsion_field_air_speed");
        Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "repulsion_field_step_height");
        return super.end(player);
    }

    public int getLV() { return 7; }
    public int getCost() { return 40; }
    public int getCooldown() { return 0; }
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new RepulsionField();
    }
}
