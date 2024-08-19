package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
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
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "repulsion_field");
        Utils.clearModifiersByName(player, SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS, "repulsion_field");
        if (getActive()) {
            playerSoul.addTag("fallImmune");
            playerSoul.setValue("jumpBoost", 2f);
            player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier("repulsion_field", playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
            player.getAttributeInstance(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS).addPersistentModifier(new EntityAttributeModifier("repulsion_field", playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "repulsion_field");
        Utils.clearModifiersByName(player, SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS, "repulsion_field");
        player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier("repulsion_field", playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        player.getAttributeInstance(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS).addPersistentModifier(new EntityAttributeModifier("repulsion_field", playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
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
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "repulsion_field");
        Utils.clearModifiersByName(player, SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS, "repulsion_field");
        return super.end(player);
    }

    public int getLV() { return 7; }

    public int getCost() { return 40; }

    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new RepulsionField();
    }
}
