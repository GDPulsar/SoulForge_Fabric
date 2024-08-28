package com.pulsar.soulforge.ability.perseverance;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class RendAsunder extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        StatusEffectInstance effect = new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, playerSoul.getEffectiveLV()*10, playerSoul.isPure() ? 1 : 0);
        DamageSource damageSource = SoulForgeDamageTypes.of(player, SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE);
        int affectedCount = 0;
        for (LivingEntity target : Utils.getEntitiesInFrontOf(player, 2f, 3f, 1f, 2f)) {
            if (!TeamUtils.canDamageEntity(player.getServer(), player, target)) continue;
            if (target.damage(damageSource, 0.5f*playerSoul.getEffectiveLV())) {
                affectedCount++;
            }
            target.addStatusEffect(effect, player);
            Utils.addAntiheal(0.4f, playerSoul.getEffectiveLV()*20, target);
        }
        playerSoul.setStyle(playerSoul.getStyle() + affectedCount * 5);
        for (int i = 0; i < 3; i ++) {
            ServerWorld serverWorld = player.getServerWorld();
            Vec3d particlePos = new Vec3d(player.getRotationVector().x, 0.5f, player.getRotationVector().z).add(player.getPos());
            serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, particlePos.x, particlePos.y+i/2f, particlePos.z, 1, 0, 0, 0, 0);
        }
        return super.cast(player);
    }

    public int getLV() { return 3; }

    public int getCost() { return 30; }

    public int getCooldown() { return 200; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new RendAsunder();
    }
}
