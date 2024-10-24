package com.pulsar.soulforge.ability.justice;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.event.GameEvent;

import java.awt.*;

public class Railcannon extends AbilityBase {
    private int timer = 0;
    private int castCount = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        timer = Math.max(5, 80-3*playerSoul.getEffectiveLV());
        castCount = 0;
        return super.cast(player);
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        timer--;
        if (timer == 0) {
            Vec3d end = player.getEyePos().add(player.getRotationVector().multiply(50f));
            HitResult hit = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(50f)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
            if (hit != null) end = hit.getPos().subtract(Utils.getArmPosition(player));
            BlastEntity blast = new BlastEntity(player.getWorld(), Utils.getArmPosition(player),
                    player, 0.25f, Vec3d.ZERO, end, playerSoul.getEffectiveLV()/4f, Color.YELLOW, true, Math.min(200, 40*playerSoul.getEffectiveLV()/3));
            blast.owner = player;
            ServerWorld serverWorld = (ServerWorld)player.getWorld();
            serverWorld.spawnEntity(blast);
            serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, player.getPos(), GameEvent.Emitter.of(player));
            serverWorld.playSoundFromEntity(null, player, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 1f, 1f);
            timer = Math.max(5, 80-3*playerSoul.getEffectiveLV());
            castCount += 1;
        }
        return castCount >= playerSoul.getEffectiveLV()/5 || castCount >= 4;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        timer = 0;
        castCount = 0;
        return super.end(player);
    }

    public int getLV() { return 7; }

    public int getCost() { return 40; }

    public int getCooldown() { return 400; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Railcannon();
    }
}
