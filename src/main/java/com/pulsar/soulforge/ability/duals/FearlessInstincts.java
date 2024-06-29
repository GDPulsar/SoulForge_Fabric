package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.ability.bravery.BraveryBoost;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FearlessInstincts extends ToggleableAbilityBase {
    public final String name = "Fearless Instincts";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "fearless_instincts");
    public final int requiredLv = 15;
    public final int cost = 100;
    public final int cooldown = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        toggleActive();
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
        return true;
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

    public String getName() { return name; }

    public Text getLocalizedText() { return Text.translatable("ability."+id.getPath()+".name"); }

    public Identifier getID() { return id; }

    public String getTooltip() { return Text.translatable("ability."+id.getPath()+".tooltip").getString(); }

    public int getLV() { return requiredLv; }

    public int getCost() { return cost; }

    public int getCooldown() { return cooldown; }

    public AbilityType getType() { return type; }

    @Override
    public AbilityBase getInstance() {
        return new FearlessInstincts();
    }
}
