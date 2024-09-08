package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.TemporaryModifierComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

import java.util.UUID;

public class AntigravityZone extends ToggleableAbilityBase {
    int timer = 0;
    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (timer % 20 == 0) {
            int affectedCount = player.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(player.getPos(), 30, 30, 30), (entity) ->
                    entity != player && player.distanceTo(entity) < 15f).size();
            playerSoul.setStyle(playerSoul.getStyle() + 3 * affectedCount);
        }
        for (LivingEntity target : player.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(player.getPos(), 30, 30, 30), (entity) ->
                player.distanceTo(entity) < 15f)) {
            float slowAmount = -0.02f * playerSoul.getEffectiveLV();
            TemporaryModifierComponent modifiers = SoulForge.getTemporaryModifiers(target);
            if (modifiers != null) {
                if (target != player) {
                    modifiers.addTemporaryModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(UUID.fromString("c70e0a30-fc03-427e-b385-9a7bdf6e6ea8"),
                            "antigravity_zone", slowAmount, EntityAttributeModifier.Operation.MULTIPLY_TOTAL), 2);
                }
                modifiers.addTemporaryModifier(SoulForgeAttributes.GRAVITY_MODIFIER, new EntityAttributeModifier(UUID.fromString("c70e0a30-fc03-427e-b385-9a7bdf6e6ea8"),
                        "antigravity_zone", -0.2, EntityAttributeModifier.Operation.MULTIPLY_TOTAL), 2);
            }
        }
        timer = (timer + 1) % 20;
        return super.tick(player);
    }

    public int getLV() { return 17; }

    public int getCost() { return 40; }

    public int getCooldown() { return 400; }

    @Override
    public AbilityBase getInstance() {
        return new AntigravityZone();
    }
}
