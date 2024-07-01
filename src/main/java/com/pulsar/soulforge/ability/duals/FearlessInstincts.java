package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FearlessInstincts extends ToggleableAbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        super.cast(player);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.removeTag("fallImmune");
        playerSoul.removeValue("jumpBoost");
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).removeModifier(Identifier.of(SoulForge.MOD_ID, "fearless_instincts_health"));
        Utils.removeModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, "fearless_instincts_health");
        Utils.removeModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "fearless_instincts_strength");
        Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "fearless_instincts_speed");
        Utils.removeModifier(player, EntityAttributes.GENERIC_STEP_HEIGHT, "fearless_instincts_step_height");
        if (isActive()) {
            if (playerSoul.getMagic() < 100f) {
                setActive(false);
                return false;
            }
            playerSoul.addTag("fallImmune");
            playerSoul.setValue("jumpBoost", 2f);
            Utils.addModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "fearless_instincts_speed", playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            Utils.addModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, "fearless_instincts_health", playerSoul.getEffectiveLV() / 40f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            Utils.addModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "fearless_instincts_strength", playerSoul.getEffectiveLV() / 40f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            Utils.addModifier(player, EntityAttributes.GENERIC_STEP_HEIGHT, "fearless_instincts_step_height", 1f, EntityAttributeModifier.Operation.ADD_VALUE);
            playerSoul.setMagic(0f);
        }
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        Utils.removeModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, "fearless_instincts_health");
        Utils.removeModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "fearless_instincts_strength");
        Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "fearless_instincts_speed");
        Utils.removeModifier(player, EntityAttributes.GENERIC_STEP_HEIGHT, "fearless_instincts_step_height");
        Utils.addModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "fearless_instincts_speed", playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        Utils.addModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, "fearless_instincts_health", playerSoul.getEffectiveLV() / 40f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        Utils.addModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "fearless_instincts_strength", playerSoul.getEffectiveLV() / 40f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        Utils.addModifier(player, EntityAttributes.GENERIC_STEP_HEIGHT, "fearless_instincts_step_height", 1f, EntityAttributeModifier.Operation.ADD_VALUE);
        playerSoul.addTag("fallImmune");
        playerSoul.setValue("jumpBoost", 2f);
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.removeTag("fallImmune");
        playerSoul.removeValue("jumpBoost");
        Utils.removeModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, "fearless_instincts_health");
        Utils.removeModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "fearless_instincts_strength");
        Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "fearless_instincts_speed");
        Utils.removeModifier(player, EntityAttributes.GENERIC_STEP_HEIGHT, "fearless_instincts_step_height");
        return true;
    }
    public int getLV() { return 15; }
    public int getCost() { return 100; }
    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new FearlessInstincts();
    }
}
