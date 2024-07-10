package com.pulsar.soulforge.ability.perseverance;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Onrush extends AbilityBase {
    private int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        damaged = new ArrayList<>();
        timer = 10;
        Vec3d vel = new Vec3d(player.getRotationVector().x, 0f, player.getRotationVector().z);
        player.addVelocity(vel.normalize());
        player.velocityModified = true;
        return super.cast(player);
    }

    List<LivingEntity> damaged = new ArrayList<>();
    int chainCount = 0;
    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer--;
        boolean hit = false;
        float damage = (float)player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue();
        damage *= 1.5f;
        DamageSource source = SoulForgeDamageTypes.of(player.getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE);
        for (Entity target : player.getEntityWorld().getOtherEntities(player, Box.of(player.getPos(), 1, 1, 1))) {
            if (target instanceof LivingEntity living) {
                if (target instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
                }
                if (!damaged.contains(living)) {
                    if (living.damage(source, damage)) {
                        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                        playerSoul.setStyle(playerSoul.getStyle() + (int)(damage * (chainCount + 1)));
                        living.timeUntilRegen += 10;
                        damaged.add(living);
                        hit = true;
                        break;
                    }
                }
            }
        }
        if (hit) {
            timer = 10;
            Vec3d vel = new Vec3d(player.getRotationVector().x, 0f, player.getRotationVector().z);
            player.addVelocity(vel.normalize());
            player.velocityModified = true;
            chainCount++;
        }
        return timer <= 0;
    }

    public int getLV() { return 7; }

    public int getCost() { return 30; }

    public int getCooldown() { return 160; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Onrush();
    }
}
