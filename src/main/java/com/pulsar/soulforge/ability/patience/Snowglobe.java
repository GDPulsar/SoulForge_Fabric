package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

import java.util.UUID;

public class Snowglobe extends AbilityBase {
    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        super.cast(player);
        if (getActive()) {
            SoulForge.getValues(player).setBool("Immobilized", true);
            timer = 200;
            Utils.addTemporaryAttribute(player, SoulForgeAttributes.DAMAGE_REDUCTION,
                    new EntityAttributeModifier(UUID.fromString("dcb6e304-2e3f-44e2-b2dd-81dd409fdaae"), "snowglobe",
                            -0.8, EntityAttributeModifier.Operation.MULTIPLY_TOTAL), 200);
        }
        return getActive();
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        timer--;
        for (PlayerEntity nearby : player.getWorld().getEntitiesByClass(PlayerEntity.class, Box.of(player.getPos(), 10, 10, 10),
                (entity) -> entity != player && TeamUtils.canHealEntity(player.getServer(), player, entity) && entity.distanceTo(player) <= 5f)) {
            Utils.addTemporaryAttribute(nearby, SoulForgeAttributes.MAGIC_POWER,
                    new EntityAttributeModifier(UUID.fromString("e8afbcd4-462a-42b0-8f9f-d7dae0e71dc7"), "snowglobe_boost",
                            playerSoul.getEffectiveLV() * 0.025f, EntityAttributeModifier.Operation.ADDITION), 2);
        }
        return super.tick(player) && timer <= 0;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        SoulForge.getValues(player).removeBool("Immobilized");
        return super.cast(player);
    }

    public int getLV() { return 15; }

    public int getCost() { return 60; }

    public int getCooldown() { return 1200; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Snowglobe();
    }
}
