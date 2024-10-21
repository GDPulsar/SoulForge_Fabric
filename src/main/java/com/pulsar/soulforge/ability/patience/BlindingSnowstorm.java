package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.TemporaryModifierComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Objects;
import java.util.UUID;

public class BlindingSnowstorm extends ToggleableAbilityBase {
    public LivingEntity frostMark;
    public UUID frostMarkUUID;
    public Vec3d location;
    public float size;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (!getActive()) {
            if (playerSoul.hasTrait(Traits.patience) && playerSoul.hasTrait(Traits.perseverance)) {
                EntityHitResult hit = Utils.getFocussedEntity(player, 10f);
                if (hit != null && hit.getEntity() instanceof LivingEntity living) {
                    size = 60f;
                    frostMark = living;
                    frostMarkUUID = living.getUuid();
                    location = living.getPos();
                } else {
                    return false;
                }
            } else {
                BlockHitResult hit = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(50f)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.SOURCE_ONLY, player));
                if (hit != null) {
                    size = 60f;
                    location = hit.getBlockPos().toCenterPos();
                } else {
                    return false;
                }
            }
        }
        return super.cast(player);
    }

    int timer = 0;
    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (location == null && frostMark == null) {
            setActive(false);
            return true;
        }
        if (frostMarkUUID != null) frostMark = (LivingEntity)player.getWorld().getEntityLookup().get(frostMarkUUID);
        if (frostMark != null) {
            if (frostMark.isDead()) {
                setActive(false);
                frostMark = null;
                frostMarkUUID = null;
                location = null;
                return false;
            }
            location = frostMark.getPos();
            SoulForge.getValues(frostMark).setTimer("FrostMarked", 3);
        }
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        float styleChange = 0f;
        for (Entity entity : player.getEntityWorld().getOtherEntities(null, Box.of(location, size*2, size*2, size*2))) {
            if (entity instanceof LivingEntity target) {
                if (target.squaredDistanceTo(location) <= size * size) {
                    target.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.SNOWED_VISION, 5, 0));
                    SoulForge.getValues(target).setUUID("SnowedBy", player.getUuid());
                    TemporaryModifierComponent modifiers = SoulForge.getTemporaryModifiers(target);
                    float level = 1f / Math.min(1f + 0.015f * playerSoul.getEffectiveLV(), 1.6f) - 1f;
                    modifiers.addTemporaryModifier(SoulForgeAttributes.EFFECT_DURATION_MULTIPLIER, new EntityAttributeModifier(
                            UUID.fromString("2ab10d5f-e29a-468e-b77b-c8faba1b16c7"), "snowstorm",
                            level, EntityAttributeModifier.Operation.MULTIPLY_TOTAL), 2);
                    if (target != player) styleChange += (1f + Utils.getTotalDebuffLevel(target) / 10f);
                }
            }
        }
        if (timer % 20 == 0) {
            playerSoul.setStyle(playerSoul.getStyle() + (int)styleChange);
        }
        timer = (timer + 1) % 20;
        ServerWorld serverWorld = player.getServer().getWorld(player.getWorld().getRegistryKey());
        float phiStep = (float) (Math.PI / 32);
        float thetaStep = (float) (2.0 * Math.PI / 64);
        for (int i = 0; i < 64; i++) {
            float phi = i * phiStep;
            for (int j = 2; j < 62; j++) {
                if (Math.random() <= 0.25) {
                    float theta = j * thetaStep;
                    float x = (float)((Math.sin(phi) * Math.cos(theta) * (size-0.5f)) + location.getX());
                    float y = (float)((Math.cos(phi) * (size-0.5f)) + location.getY());
                    float z = (float)((Math.sin(phi) * Math.sin(theta) * (size-0.5f)) + location.getZ());
                    serverWorld.spawnParticles(ParticleTypes.SNOWFLAKE, x, y, z, 1, 0.5, 0.5, 0.5, 0);
                }
            }
        }
        if (player.hasStatusEffect(SoulForgeEffects.MANA_OVERLOAD)) setActive(false);
        return super.tick(player);
    }

    public int getLV() { return 12; }

    public int getCost() { return 40; }

    public int getCooldown() { return 400; }

    @Override
    public AbilityBase getInstance() {
        return new BlindingSnowstorm();
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        if (location != null) nbt.put("location", Utils.vectorToNbt(location));
        if (frostMark != null && frostMarkUUID != null) nbt.putUuid("frostMark", frostMarkUUID);
        nbt.putFloat("size", size);
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        if (nbt.contains("location")) location = Utils.nbtToVector(nbt.getList("location", NbtElement.DOUBLE_TYPE));
        if (nbt.contains("frostMark")) frostMarkUUID = nbt.getUuid("frostMark");
        size = nbt.getFloat("size");
        super.readNbt(nbt);
    }
}
