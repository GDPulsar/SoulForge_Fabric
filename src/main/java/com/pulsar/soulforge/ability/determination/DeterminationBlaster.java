package com.pulsar.soulforge.ability.determination;

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

public class DeterminationBlaster extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        Vec3d end = player.getEyePos().add(player.getRotationVector().multiply(30f));
        HitResult hit = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(30f)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
        if (hit != null) end = hit.getPos().subtract(Utils.getArmPosition(player));
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        BlastEntity blast = new BlastEntity(player.getWorld(), Utils.getArmPosition(player),
                player, 0.25f, Vec3d.ZERO, end, playerSoul.getEffectiveLV()*0.75f, Color.RED);
        blast.owner = player;
        ServerWorld serverWorld = (ServerWorld)player.getWorld();
        serverWorld.spawnEntity(blast);
        serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, player.getPos(), GameEvent.Emitter.of(player));
        serverWorld.playSoundFromEntity(null, player, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        return super.cast(player);
    }

    public int getLV() { return 15; }

    public int getCost() { return 50; }

    public int getCooldown() { return 300; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new DeterminationBlaster();
    }
}
