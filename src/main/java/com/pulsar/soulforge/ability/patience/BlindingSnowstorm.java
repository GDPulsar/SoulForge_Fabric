package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.ability.bravery.BraveryBoost;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Vector3f;

import java.util.Objects;

public class BlindingSnowstorm extends ToggleableAbilityBase {
    public final String name = "Blinding Snowstorm";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "blinding_snowstorm");
    public final int requiredLv = 12;
    public final int cost = 40;
    public final int cooldown = 400;
    
    public BlockPos location;
    public float size;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        toggleActive();
        if (getActive()) {
            BlockHitResult hit = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(50f)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.SOURCE_ONLY, player));
            if (hit != null) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                size = playerSoul.getTraits().contains(Traits.patience) && playerSoul.getTraits().contains(Traits.perseverance) ? 140f : 35f;
                location = hit.getBlockPos();
            }
        }
        return getActive();
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        for (Entity entity : player.getEntityWorld().getOtherEntities(null, Box.of(location.toCenterPos(), size*2, size*2, size*2))) {
            if (entity instanceof LivingEntity target) {
                if (target.squaredDistanceTo(location.toCenterPos()) <= size * size) {
                    target.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.SNOWED_VISION, 5, 0));
                }
            }
        }
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
        if (player.hasStatusEffect(SoulForgeEffects.MANA_OVERLOAD)) return true;
        return !getActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
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
        return new BlindingSnowstorm();
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.put("location", NbtHelper.fromBlockPos(location));
        nbt.putFloat("size", size);
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        location = NbtHelper.toBlockPos(nbt.getCompound("location"));
        size = nbt.getFloat("size");
    }
}
