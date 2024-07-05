package com.pulsar.soulforge.ability.justice;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.entity.AutoTurretEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;
import java.util.UUID;

public class AutoTurret extends AbilityBase {
    private AutoTurretEntity turret;
    private UUID turretUUID;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (turretUUID != null && turret == null) {
            Entity found = player.getServerWorld().getEntity(turretUUID);
            if (found instanceof AutoTurretEntity) {
                turret = (AutoTurretEntity)found;
            } else {
                SoulForge.LOGGER.warn("Turret UUID did not match an instance of a turret. Found " + found.getClass() + " instead.");
                turret = null;
                turretUUID = null;
            }
        }
        if (turret != null) {
            if (turret.isAlive() || !turret.isRemoved()) {
                turret.kill();
                turret.remove(Entity.RemovalReason.DISCARDED);
            }
            turret = null;
        }
        turret = new AutoTurretEntity(player.getWorld());
        turret.direction = new Vec3d(player.getRotationVector().x, 0f, player.getRotationVector().z).normalize();
        turret.setPosition(player.getPos().add(turret.direction));
        player.getWorld().spawnEntity(turret);
        turretUUID = turret.getUuid();
        player.getWorld().playSoundFromEntity(null, player, SoulForgeSounds.WEAPON_SUMMON_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        return true;
    }

    public int getLV() { return 12; }

    public int getCost() { return 30; }

    public int getCooldown() { return 2400; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new AutoTurret();
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        if (turret != null) nbt.putUuid("turret", turret.getUuid());
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        if (nbt.contains("turret")) {
            turretUUID = nbt.getUuid("turret");
        }
    }
}
