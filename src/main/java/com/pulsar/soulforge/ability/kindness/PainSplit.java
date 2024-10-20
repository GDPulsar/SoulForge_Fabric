package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

public class PainSplit extends ToggleableAbilityBase {
    public LivingEntity target = null;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (target == null) {
            EntityHitResult hit = Utils.getFocussedEntity(player, 5);
            if (hit != null) {
                Entity entity = hit.getEntity();
                if (entity instanceof LivingEntity living) {
                    if (!TeamUtils.canHealEntity(player.getServer(), player, living)) return false;
                    target = living;
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.UT_HEAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                    return super.cast(player);
                }
            }
        } else {
            target = null;
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (target != null) {
            if (target.distanceTo(player) >= 300f) target = null;
        }
        return target == null;
    }

    @Override
    public void displayTick(PlayerEntity player) {
        if (target != null) {
            player.getWorld().addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00FF00).toVector3f(), 1f),
                    target.getX() + Math.random()*0.8f - 0.4f, target.getY() + Math.random()*2, target.getZ() + Math.random()*0.8f - 0.4f, 0f, 0f, 0f);
        }
    }

    public int getLV() { return 15; }

    public int getCost() { return 40; }

    public int getCooldown() { return 200; }

    public AbilityType getType() { return AbilityType.TOGGLE; }

    @Override
    public AbilityBase getInstance() {
        return new PainSplit();
    }
}
