package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.RaycastContext;

import java.util.Objects;

public class BlindingSnowstorm extends ToggleableAbilityBase {
    public BlockPos location;
    public float size;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        super.cast(player);
        if (getActive()) {
            BlockHitResult hit = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(50f)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.SOURCE_ONLY, player));
            if (hit != null) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                size = playerSoul.getTraits().contains(Traits.patience) && playerSoul.getTraits().contains(Traits.perseverance) ? 140f : 35f;
                location = hit.getBlockPos();
            }
        }
        return true;
    }

    int timer = 0;
    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (location == null) {
            setActive(false);
            return true;
        }
        float styleChange = 0f;
        for (Entity entity : player.getEntityWorld().getOtherEntities(null, Box.of(location.toCenterPos(), size*2, size*2, size*2))) {
            if (entity instanceof LivingEntity target) {
                if (target.squaredDistanceTo(location.toCenterPos()) <= size * size) {
                    target.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.SNOWED_VISION, 5, 0));
                }
                if (target != player) styleChange += (1f + Utils.getTotalDebuffLevel(target) / 10f);
            }
        }
        if (timer % 20 == 0) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
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
                    float x = (float)(Math.sin(phi) * Math.cos(theta) * (size-0.5f)) + location.getX();
                    float y = (float)(Math.cos(phi) * (size-0.5f)) + location.getY();
                    float z = (float)(Math.sin(phi) * Math.sin(theta) * (size-0.5f)) + location.getZ();
                    serverWorld.spawnParticles(ParticleTypes.SNOWFLAKE, x, y, z, 1, 0.5, 0.5, 0.5, 0);
                }
            }
        }
        if (player.hasStatusEffect(SoulForgeEffects.MANA_SICKNESS)) setActive(false);
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
        if (nbt.contains("location")) nbt.put("location", NbtHelper.fromBlockPos(location));
        nbt.putFloat("size", size);
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        if (location != null) location = NbtHelper.toBlockPos(nbt.getCompound("location"));
        size = nbt.getFloat("size");
        super.readNbt(nbt);
    }
}
