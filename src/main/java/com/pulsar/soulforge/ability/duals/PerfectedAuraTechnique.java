package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.Objects;

public class PerfectedAuraTechnique extends ToggleableAbilityBase {
    public boolean fullPower = false;
    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        super.cast(player);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (isActive()) {
            if (playerSoul.getMagic() < 100f) {
                setActive(false);
                return false;
            }
            playerSoul.setMagic(0f);
        }
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (fullPower) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if (timer == 5020) {
                player.setHealth(60f);
                playerSoul.setMagic(100f);
            }
            Utils.removeModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, "pat_health");
            Utils.removeModifier(player, EntityAttributes.GENERIC_ARMOR, "pat_armor");
            Utils.removeModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "pat_strength");
            Utils.removeModifier(player, SoulForgeAttributes.MAGIC_POWER, "pat_magic");
            Utils.removeModifier(player, SoulForgeAttributes.MAGIC_COOLDOWN, "pat_cooldown");
            Utils.removeModifier(player, SoulForgeAttributes.MAGIC_COST, "pat_cost");
            Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "pat_speed");
            Utils.addModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, "pat_health", 2f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            Utils.addModifier(player, EntityAttributes.GENERIC_ARMOR, "pat_armor", 1f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            Utils.addModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "pat_strength", 1f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            Utils.addModifier(player, SoulForgeAttributes.MAGIC_POWER, "pat_magic", 1f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            Utils.addModifier(player, SoulForgeAttributes.MAGIC_COOLDOWN, "pat_cooldown", -0.5f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            Utils.addModifier(player, SoulForgeAttributes.MAGIC_COST, "pat_cost", -0.5f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            Utils.addModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "pat_speed", 0.15f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            for (Entity entity : player.getEntityWorld().getOtherEntities(player, Box.of(player.getPos(), 20, 20, 20))) {
                if (entity instanceof LivingEntity target) {
                    if (target.distanceTo(player) < 10f) {
                        if (entity.getFireTicks() < 40) { entity.setFireTicks(50); }
                    }
                }
            }
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 12, 2));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 10, 1));
            player.removeStatusEffect(SoulForgeEffects.MANA_OVERLOAD);
            timer--;
            if (timer <= 0) {
                fullPower = false;
                Utils.removeModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, "pat_health");
                Utils.removeModifier(player, EntityAttributes.GENERIC_ARMOR, "pat_armor");
                Utils.removeModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "pat_strength");
                Utils.removeModifier(player, SoulForgeAttributes.MAGIC_POWER, "pat_magic");
                Utils.removeModifier(player, SoulForgeAttributes.MAGIC_COOLDOWN, "pat_cooldown");
                Utils.removeModifier(player, SoulForgeAttributes.MAGIC_COST, "pat_cost");
                Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "pat_speed");
                player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_OVERLOAD, 900, 4));
            }
            ServerWorld serverWorld = player.getServerWorld();
            for (ServerPlayerEntity target : serverWorld.getPlayers()) {
                if (target != player) {
                    player.getServerWorld().spawnParticles(target, ParticleTypes.FLAME, false, player.getX(), player.getY(), player.getZ(), 25, 0.5, 1, 0.5, 0.25f);
                    player.getServerWorld().spawnParticles(target, ParticleTypes.DRAGON_BREATH, false, player.getX(), player.getY(), player.getZ(), 25, 0.5, 1, 0.5, 0.25f);
                }
            }
        } else {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            Utils.removeModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, "pat_health");
            Utils.removeModifier(player, EntityAttributes.GENERIC_ARMOR, "pat_armor");
            Utils.removeModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "pat_strength");
            Utils.addModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, "pat_health", playerSoul.getEffectiveLV() / 2f, EntityAttributeModifier.Operation.ADD_VALUE);
            Utils.addModifier(player, EntityAttributes.GENERIC_ARMOR, "pat_armor", playerSoul.getEffectiveLV() * 0.0175f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            Utils.addModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "pat_strength", playerSoul.getEffectiveLV() / 10f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Utils.removeModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, "pat_health");
        Utils.removeModifier(player, EntityAttributes.GENERIC_ARMOR, "pat_armor");
        Utils.removeModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "pat_strength");
        Utils.removeModifier(player, SoulForgeAttributes.MAGIC_POWER, "pat_magic");
        Utils.removeModifier(player, SoulForgeAttributes.MAGIC_COOLDOWN, "pat_cooldown");
        Utils.removeModifier(player, SoulForgeAttributes.MAGIC_COST, "pat_cost");
        Utils.removeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "pat_speed");
        return super.end(player);
    }

    public int getLV() { return 15; }
    public int getCost() { return 100; }
    public int getCooldown() { return 0; }

    @Override
    public AbilityBase getInstance() {
        return new PerfectedAuraTechnique();
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.putBoolean("fullPower", fullPower);
        nbt.putInt("timer", timer);
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        fullPower = nbt.getBoolean("fullPower");
        timer = nbt.getInt("timer");
        super.readNbt(nbt);
    }
}
