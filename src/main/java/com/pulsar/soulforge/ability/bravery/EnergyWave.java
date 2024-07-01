package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.event.GameEvent;

import java.awt.*;
import java.util.Objects;

public class EnergyWave extends ToggleableAbilityBase {
    private int chargeTimer = 0;
    private int chargeLevel = 0;
    private boolean overcharged = false;

    private final EntityAttributeModifier modifier = new EntityAttributeModifier(Identifier.of(SoulForge.MOD_ID, "energy_wave"), -0.7, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    @Override
    public boolean cast(ServerPlayerEntity player) {
        super.cast(player);
        if (!isActive()) {
            Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).addPersistentModifier(modifier);
            chargeLevel = 0;
            chargeTimer = 0;
        }
        return isActive();
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
                if (playerSoul.getLV() >= 10) {
                    if (playerSoul.hasValue("overchargeCooldown")) {
                        if (playerSoul.getValue("overchargeCooldown") > 0) return true;
                    }
                    playerSoul.setMagic(0f);
                    overcharged = true;
                } else {
                    return true;
                }
            }
            chargeLevel += 1;
        }
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        setActive(false);
        Vec3d end = player.getEyePos().add(player.getRotationVector().multiply(30f));
        HitResult hit = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(30f)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
        if (hit != null) end = hit.getPos().subtract(Utils.getArmPosition(player));
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        float damage = 0.4f * playerSoul.getEffectiveLV();
        float size = 0.25f;
        if (overcharged) {
            damage = 1.6f * playerSoul.getEffectiveLV();
            size = 0.5f;
            player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 2400, 1));
            playerSoul.setValue("overchargeCooldown", 6000);
        }
        BlastEntity blast = new BlastEntity(player.getWorld(), Utils.getArmPosition(player),
                player, size, Vec3d.ZERO, end, damage, Color.ORANGE);
        blast.owner = player;
        ServerWorld serverWorld = (ServerWorld)player.getWorld();
        serverWorld.spawnEntity(blast);
        serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, player.getPos(), GameEvent.Emitter.of(player));
        serverWorld.playSoundFromEntity(null, player, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        playerSoul.setCooldown(this, 100+20*chargeLevel);
        playerSoul.setValue("energyWaveCooldown", 100 + 20*chargeLevel);
        Objects.requireNonNull(player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).removeModifier(Identifier.of( SoulForge.MOD_ID, "energy_wave"));
        return true;
    }

    public String getName() { return "Energy Wave"; }

    public int getLV() { return 3; }

    public int getCost() { return 20; }

    public int getCooldown() { return 0; }

    @Override
    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new EnergyWave();
    }
}
