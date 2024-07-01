package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class Warpspeed extends AbilityBase {
    private int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        timer = 300;
        Utils.addModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "warpspeed_speed", 2f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        Utils.addModifier(player, SoulForgeAttributes.AIR_SPEED, "warpspeed_air_speed", 2f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        Utils.addModifier(player, EntityAttributes.GENERIC_STEP_HEIGHT, "warpspeed_step_height", 3f, EntityAttributeModifier.Operation.ADD_VALUE);
        return true;
    }

    private Vec3d lastPos = Vec3d.ZERO;
    private int stillTimer = 0;

    @Override
    public boolean tick(ServerPlayerEntity player) {
        for (Entity entity : player.getEntityWorld().getOtherEntities(player, new Box(player.getPos().subtract(1, 1, 1), player.getPos().add(1, 1, 1)))) {
            if (entity instanceof PlayerEntity targetPlayer) {
                if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) return false;
            }
            entity.damage(SoulForgeDamageTypes.of(player.getWorld(), SoulForgeDamageTypes.WARPSPEED_DAMAGE_TYPE), player.getMovementSpeed()*8f);
            entity.setVelocity(player.getRotationVector().x*5f, 1.5f, player.getRotationVector().z*5f);
            entity.velocityModified = true;
        }
        timer--;
        if (lastPos.distanceTo(player.getPos()) < 0.001f) {
            stillTimer++;
            if (stillTimer >= 4) {
                timer = 0;
            }
        } else {
            stillTimer = 0;
        }
        lastPos = player.getPos();
        return timer <= 0;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Utils.removeModifier(player, EntityAttributes.GENERIC_STEP_HEIGHT, "warpspeed_step_height");
        Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "warpspeed_speed");
        Utils.removeModifier(player, SoulForgeAttributes.AIR_SPEED, "warpspeed_air_speed");
        player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 1800, 1));
        return true;
    }

    public int getLV() { return 20; }
    public int getCost() { return 100; }
    public int getCooldown() { return 6000; }
    public AbilityType getType() { return AbilityType.SPECIAL; }

    @Override
    public AbilityBase getInstance() {
        return new Warpspeed();
    }
}
