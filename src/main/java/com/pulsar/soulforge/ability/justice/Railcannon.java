package com.pulsar.soulforge.ability.justice;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.entity.JusticePelletProjectile;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.awt.*;

public class Railcannon extends AbilityBase {
    public final String name = "Railcannon";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "railcannon");
    public final int requiredLv = 7;
    public final int cost = 40;
    public final int cooldown = 400;
    public final AbilityType type = AbilityType.CAST;

    private int timer = 0;
    private int castCount = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        timer = Math.max(5, 80-3*playerSoul.getEffectiveLV());
        castCount = 0;
        return true;
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
        return new Railcannon();
    }
}
