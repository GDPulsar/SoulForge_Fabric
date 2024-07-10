package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

public class AllyHeal extends AbilityBase {
    int timer = 0;
    LivingEntity target = null;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult result = Utils.getFocussedEntity(player, 15);
        if (result != null) {
            if (result.getEntity() instanceof LivingEntity) {
                target = (LivingEntity)result.getEntity();
                if (target instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canHealPlayer(player.getServer(), player, targetPlayer)) return false;
                }
                timer = 200;
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoulForgeSounds.HEAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                for (int i = 0; i < 20; i++) {
                    player.getServerWorld().addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00FF00).toVector3f(), 1f),
                            target.getX() + Math.random()*2-1,
                            target.getY() + Math.random()*2-1,
                            target.getZ() + Math.random()*2-1,
                            0, 0, 0);
                }
                return super.cast(player);
            }
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (target != null) {
            if (target.age - target.getLastAttackedTime() >= 0 && target.age - target.getLastAttackedTime() <= 1) return true;
            if (timer > 0) timer--;
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            int rate = Math.round(120f / (Math.min(36, playerSoul.getEffectiveLV() + 6)));
            if (timer % rate == 0) {
                if (target.getHealth() < target.getMaxHealth()) playerSoul.setStyle(playerSoul.getStyle() + 1);
                target.heal(1f);
            }
            return timer <= 0;
        }
        return super.tick(player);
    }

    public int getLV() { return 1; }

    public int getCost() { return 20; }

    public int getCooldown() { return 300; }

    public AbilityType getType() { return AbilityType.CAST; }
    @Override
    public AbilityBase getInstance() {
        return new AllyHeal();
    }

}
