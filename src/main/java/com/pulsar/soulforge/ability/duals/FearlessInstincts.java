package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

public class FearlessInstincts extends ToggleableAbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.removeTag("fallImmune");
        playerSoul.removeValue("jumpBoost");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "fearless_instincts_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "fearless_instincts_strength");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "fearless_instincts_speed");
        if (getActive()) {
            if (playerSoul.getMagic() < 100f) {
                setActive(false);
                return false;
            }
            playerSoul.addTag("fallImmune");
            playerSoul.setValue("jumpBoost", 2f);
            player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier("fearless_instincts_speed", playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
            player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(new EntityAttributeModifier("fearless_instincts_health", playerSoul.getEffectiveLV() / 40f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
            player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(new EntityAttributeModifier("fearless_instincts_strength", playerSoul.getEffectiveLV() / 40f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
            playerSoul.setMagic(0f);
        }
        super.cast(player);
        return getActive();
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "fearless_instincts_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "fearless_instincts_strength");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "fearless_instincts_speed");
        player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier("fearless_instincts_speed", playerSoul.getEffectiveLV() * 0.0266f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(new EntityAttributeModifier("fearless_instincts_health", playerSoul.getEffectiveLV() / 40f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(new EntityAttributeModifier("fearless_instincts_strength", playerSoul.getEffectiveLV() / 40f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        if (!playerSoul.hasCast("Warpspeed")) player.setStepHeight(1.6f);
        playerSoul.addTag("fallImmune");
        playerSoul.setValue("jumpBoost", 2f);
        return !getActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.removeTag("fallImmune");
        playerSoul.removeValue("jumpBoost");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "fearless_instincts_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "fearless_instincts_strength");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "fearless_instincts_speed");
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
