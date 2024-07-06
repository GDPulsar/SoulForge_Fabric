package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;

public class Iceshock extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult hit = Utils.getFocussedEntity(player, 10);
        if (hit != null) {
            if (hit.getEntity() instanceof LivingEntity target) {
                if (target instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) return false;
                }
                player.getServerWorld().spawnParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getY(), target.getZ(), 20, 0.5, 1, 0.5, 0.1);
                player.getServerWorld().playSoundFromEntity(null, player, SoulForgeSounds.DR_ICESHOCK_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                target.damage(SoulForgeDamageTypes.of(player, SoulForgeDamageTypes.ABILITY_PIERCE_DAMAGE_TYPE), 3f);
                return super.cast(player);
            }
        }
        return false;
    }

    public int getLV() { return 1; }

    public int getCost() { return 20; }

    public int getCooldown() { return 200; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Iceshock();
    }
}
