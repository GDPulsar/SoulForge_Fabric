package com.pulsar.soulforge.ability.justice;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.TemporaryModifierComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Launch extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        TemporaryModifierComponent modifiers = SoulForge.getTemporaryModifiers(player);
        if (modifiers != null) {
            modifiers.addTemporaryModifier(SoulForgeAttributes.FALL_DAMAGE_MULTIPLIER, new EntityAttributeModifier("launch", -1f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL), 80);
        }
        player.addVelocity(new Vec3d(0, 2, 0));
        player.velocityModified = true;
        return super.cast(player);
    }

    public int getLV() { return 15; }

    public int getCost() { return 15; }

    public int getCooldown() { return 200; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Launch();
    }
}
