package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class GravityAnchor extends AbilityBase {
    private int timer = 0;
    private LivingEntity target;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult result = Utils.getFocussedEntity(player, 15);
        if (result != null && result.getEntity() instanceof LivingEntity living) {
            if (living instanceof PlayerEntity targetPlayer) {
                if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) return false;
            }
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            target = living;
            for (int i = 0; i < 10; i++) {
                float x = MathHelper.sin((float)(i/5*Math.PI));
                float z = MathHelper.cos((float)(i/5*Math.PI));
                player.getWorld().addParticle(
                        new DustParticleEffect(Vec3d.unpackRgb(0x0000FF).toVector3f(), 1f),
                        target.getX()+x, target.getY(), target.getZ()+z, 1, 0, 0.2);
            }
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
            timer = Math.round(playerSoul.getEffectiveLV()*15f);
            return super.cast(player);
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        timer--;
        if (target != null) {
            target.addVelocity(0, -5, 0);
            target.velocityModified = true;
        }
        return timer == 0;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        return super.end(player);
    }

    public int getLV() { return 5; }

    public int getCost() { return 40; }

    public int getCooldown() { return 400; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new GravityAnchor();
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.putInt("timer", timer);
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        timer = nbt.getInt("timer");
    }
}
