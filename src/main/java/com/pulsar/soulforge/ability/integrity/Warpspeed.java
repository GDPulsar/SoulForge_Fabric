package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class Warpspeed extends AbilityBase {
    public final String name = "Warpspeed";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "warpspeed");
    public final int requiredLv = 20;
    public final int cost = 100;
    public final int cooldown = 6000;
    public final AbilityType type = AbilityType.CAST;

    private int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        timer = 300;
        player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier(UUID.randomUUID(), "Warpspeed", 2f, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        player.getAttributeInstance(SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS).addPersistentModifier(new EntityAttributeModifier(UUID.randomUUID(), "Warpspeed", 2f, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        return true;
    }

    private Vec3d lastPos = Vec3d.ZERO;
    private int stillTimer = 0;

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.hasCast("Repulsion Field")) player.setStepHeight(4.6f);
        else if (playerSoul.hasCast("Accelerated Pellet Aura")) player.setStepHeight(4.6f);
        else if (playerSoul.hasCast("Fearless Instincts")) player.setStepHeight(4.6f);
        else player.setStepHeight(3.6f);
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
        player.setStepHeight(0.6f);
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "Warpspeed");
        Utils.clearModifiersByName(player, SoulForgeAttributes.AIR_SPEED_BECAUSE_MOJANG_SUCKS, "Warpspeed");
        player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 1800, 1));
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
        return new Warpspeed();
    }
}
