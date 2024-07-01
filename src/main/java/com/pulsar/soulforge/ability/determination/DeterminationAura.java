package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public class DeterminationAura extends ToggleableAbilityBase {
    int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        super.cast(player);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (isActive()) {
            playerSoul.setMagic(0f);
            timer = 0;
        }
        return isActive();
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
        if (player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER) != null) multiplier = (float) Objects.requireNonNull(player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER)).getValue();
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).removeModifier(Identifier.of("determination_aura_health"));
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).removeModifier(Identifier.of("determination_aura_strength"));
        EntityAttributeModifier healthModifier = new EntityAttributeModifier(Identifier.of("determination_aura_health"), MathHelper.floor(effLv*multiplier)/2f, EntityAttributeModifier.Operation.ADD_VALUE);
        EntityAttributeModifier strengthModifier = new EntityAttributeModifier(Identifier.of("determination_aura_strength"), playerSoul.getEffectiveLV()*0.0175f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).addPersistentModifier(healthModifier);
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).addPersistentModifier(strengthModifier);
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).removeModifier(Identifier.of("determination_aura_health"));
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).removeModifier(Identifier.of("determination_aura_strength"));
        return super.end(player);
    }

    public int getLV() { return 19; }

    public int getCost() { return 100; }

    public int getCooldown() { return 0; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new DeterminationAura();
    }
}
