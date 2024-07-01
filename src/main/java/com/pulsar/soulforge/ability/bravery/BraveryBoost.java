package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class BraveryBoost extends ToggleableAbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        super.cast(player);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (isActive()) {
            if (playerSoul.getMagic() < 100f) {
                setActive(false);
                return false;
            }
            playerSoul.setMagic(0f);
        }
        return isActive();
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).removeModifier(Identifier.of("bravery_boost_health"));
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).removeModifier(Identifier.of("bravery_boost_strength"));
        EntityAttributeModifier healthModifier = new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "bravery_boost_health"), playerSoul.getEffectiveLV() / 40f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        EntityAttributeModifier strengthModifier = new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "bravery_boost_strength"), playerSoul.getEffectiveLV() / 40f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).addPersistentModifier(healthModifier);
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).addPersistentModifier(strengthModifier);
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).removeModifier(Identifier.of("bravery_boost_health"));
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).removeModifier(Identifier.of("bravery_boost_strength"));
        return super.end(player);
    }
    
    public String getName() { return "Bravery Boost"; }

    public int getLV() { return 15; }

    public int getCost() { return 100; }

    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new BraveryBoost();
    }
}
