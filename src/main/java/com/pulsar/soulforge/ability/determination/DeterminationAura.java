package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class DeterminationAura extends ToggleableAbilityBase {
    int timer = 0;

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
        timer++;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (timer == 20) {
            player.heal(1f);
            timer = 0;
        }
        float effLv = playerSoul.getLV();
        float multiplier = 1f;
        if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER) != null) multiplier = (float)player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).getValue();
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "determination_aura_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "determination_aura_strength");
        EntityAttributeModifier healthModifier = new EntityAttributeModifier("determination_aura_health", MathHelper.floor(effLv*multiplier)/2f, EntityAttributeModifier.Operation.ADDITION);
        EntityAttributeModifier strengthModifier = new EntityAttributeModifier("determination_aura_strength", playerSoul.getEffectiveLV()*0.0175f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(healthModifier);
        player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(strengthModifier);
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "determination_aura_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "determination_aura_strength");
        return super.end(player);
    }

    public int getLV() { return 19; }

    public int getCost() { return 100; }

    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new DeterminationAura();
    }
}
