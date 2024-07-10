package com.pulsar.soulforge.ability.justice;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.OrbitalStrikeEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Objects;

public class OrbitalStrike extends AbilityBase {
    public OrbitalStrikeEntity entity;
    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (playerSoul.getStyleRank() < 4) {
            player.sendMessageToClient(Text.translatable(Math.random() < 0.01f ? "soulforge.style.get_real" : "soulforge.style.not_enough"), true);
            return false;
        }
        Vec3d start = player.getEyePos();
        Vec3d end = start.add(player.getRotationVector().x * 50f, player.getRotationVector().y * 50f, player.getRotationVector().z * 50f);
        BlockHitResult blockHit = player.getWorld().raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
        if (blockHit.getBlockPos().isWithinDistance(player.getPos(), 50f)) {
            entity = new OrbitalStrikeEntity(player.getWorld(), blockHit.getPos(), player);
            entity.owner = player;
            ServerWorld serverWorld = (ServerWorld)player.getWorld();
            serverWorld.spawnEntity(entity);
            player.getWorld().playSoundFromEntity(null, player, SoulForgeSounds.DR_REVIVAL_EVENT, SoundCategory.PLAYERS, 1f, 1f);
            timer = 300;
            player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 1800, 1));
            return super.cast(player);
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (entity != null) {
            Vec3d start = player.getEyePos();
            Vec3d end = start.add(player.getRotationVector().x * 50f, player.getRotationVector().y * 50f, player.getRotationVector().z * 50f);
            BlockHitResult blockHit = player.getWorld().raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
            Vec3d direction = blockHit.getPos().withAxis(Direction.Axis.Y, 0).subtract(entity.getPos().withAxis(Direction.Axis.Y, 0));
            direction = direction.normalize().multiply(Math.min(0.4f, direction.length()));
            entity.setPos(entity.getPos().add(direction));
        }
        timer--;
        return timer <= 0;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        return super.end(player);
    }

    public int getLV() { return 20; }

    public int getCost() { return 50; }

    public int getCooldown() { return 6000; }

    public AbilityType getType() { return AbilityType.SPECIAL; }

    @Override
    public AbilityBase getInstance() {
        return new OrbitalStrike();
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
        super.readNbt(nbt);
    }
}
