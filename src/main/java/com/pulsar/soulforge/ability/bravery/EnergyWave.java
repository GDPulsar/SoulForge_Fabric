package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.awt.*;

public class EnergyWave extends ToggleableAbilityBase {
    private int chargeTimer = 0;
    private int chargeLevel = 0;
    private int lastCooldownLength = 0;

    private final EntityAttributeModifier modifier = new EntityAttributeModifier("energy_wave", -0.7, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

    @Override
    public boolean cast(ServerPlayerEntity player) {
        if (!getActive()) {
            player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(modifier);
            chargeLevel = 0;
            chargeTimer = 0;
            super.cast(player);
        } else {
            setActive(false);
        }
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.resetLastCastTime();
        boolean valiantHeart = playerSoul.hasCast("Valiant Heart");
        chargeTimer++;
        if (chargeTimer % (valiantHeart ? 10 : 20) == 0) {
            if (playerSoul.getMagic() >= 5f) {
                playerSoul.setMagic(playerSoul.getMagic()-5f);
            } else {
                if (playerSoul.getLV() >= 10 && playerSoul.getStyleRank() >= 3) {
                    playerSoul.setMagic(0f);
                } else {
                    setActive(false);
                    return true;
                }
            }
            chargeLevel += 1;
        }
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Vec3d end = player.getEyePos().add(player.getRotationVector().multiply(30f));
        HitResult hit = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(30f)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
        if (hit != null) end = hit.getPos().subtract(Utils.getArmPosition(player));
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        float damage = 0.4f * playerSoul.getEffectiveLV();
        float size = 0.25f;
        if (playerSoul.getMagic() == 0f) {
            damage = 1.6f * playerSoul.getEffectiveLV();
            size = 0.5f;
            playerSoul.setStyleRank(Math.max(0, playerSoul.getStyleRank()-3));
        }
        BlastEntity blast = new BlastEntity(player.getWorld(), Utils.getArmPosition(player),
                player, size, Vec3d.ZERO, end, damage, new Color(255, 128, 0));
        blast.owner = player;
        ServerWorld serverWorld = (ServerWorld)player.getWorld();
        serverWorld.spawnEntity(blast);
        serverWorld.playSoundFromEntity(null, player, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        lastCooldownLength = 100 + 20 * chargeLevel;
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "energy_wave");
        return super.end(player);
    }

    public int getLV() { return 3; }

    public int getCost() { return 20; }

    public int getCooldown() { return lastCooldownLength; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new EnergyWave();
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        super.saveNbt(nbt);
        nbt.putInt("lastCooldownLength", lastCooldownLength);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        lastCooldownLength = nbt.getInt("lastCooldownLength");
    }
}
