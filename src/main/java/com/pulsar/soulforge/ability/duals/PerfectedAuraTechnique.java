package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AuraAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

public class PerfectedAuraTechnique extends AuraAbilityBase {
    @Override
    public HashMap<EntityAttribute, EntityAttributeModifier> getModifiers(int elv) {
        if (fullPower) {
            return new HashMap<>(Map.ofEntries(
                    entry(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier("perfected_aura_technique", Math.max(40, elv / 2f), EntityAttributeModifier.Operation.ADDITION)),
                    entry(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier("perfected_aura_technique", Math.max(1, elv * 0.0175f), EntityAttributeModifier.Operation.MULTIPLY_TOTAL)),
                    entry(SoulForgeAttributes.MAGIC_POWER, new EntityAttributeModifier("perfected_aura_technique", 1f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)),
                    entry(SoulForgeAttributes.MAGIC_COST, new EntityAttributeModifier("perfected_aura_technique", -0.5f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)),
                    entry(SoulForgeAttributes.MAGIC_COOLDOWN, new EntityAttributeModifier("perfected_aura_technique", -0.5f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)),
                    entry(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier("perfected_aura_technique", 0.15f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)),
                    entry(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier("perfected_aura_technique", elv * 0.01f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL))
            ));
        }
        return new HashMap<>(Map.ofEntries(
                entry(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier("perfected_aura_technique", elv / 2f, EntityAttributeModifier.Operation.ADDITION)),
                entry(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier("perfected_aura_technique", elv * 0.0175f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)),
                entry(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier("perfected_aura_technique", elv * 0.01f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL))
        ));
    }

    public boolean fullPower = false;
    public int timer = 0;

    @Override
    public boolean tick(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (fullPower) {
            if (timer == 5020) {
                player.setHealth(60f);
                playerSoul.setMagic(100f);
            }
            for (Entity entity : player.getEntityWorld().getOtherEntities(player, Box.of(player.getPos(), 20, 20, 20))) {
                if (entity instanceof LivingEntity target) {
                    if (!TeamUtils.canDamageEntity(player.getServer(), player, target)) continue;
                    if (target.distanceTo(player) < 10f) {
                        if (entity.getFireTicks() < 40) { entity.setFireTicks(50); }
                    }
                }
            }
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 12, 2));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 10, 1));
            player.removeStatusEffect(SoulForgeEffects.MANA_SICKNESS);
            timer--;
            if (timer <= 0) {
                fullPower = false;
                Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "pat_health");
                Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ARMOR, "pat_armor");
                Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "pat_strength");
                Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_POWER, "pat_magic");
                Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_COOLDOWN, "pat_cooldown");
                Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_COST, "pat_cost");
                Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "pat_speed");
                player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_SICKNESS, 900, 4));
            }
            if (timer % 20 == 0) {
                playerSoul.setStyle(playerSoul.getStyle() + 1);
            }
            ServerWorld serverWorld = player.getServerWorld();
            for (ServerPlayerEntity target : serverWorld.getPlayers()) {
                if (target != player) {
                    player.getServerWorld().spawnParticles(target, ParticleTypes.FLAME, false, player.getX(), player.getY(), player.getZ(), 25, 0.5, 1, 0.5, 0.25f);
                    player.getServerWorld().spawnParticles(target, ParticleTypes.DRAGON_BREATH, false, player.getX(), player.getY(), player.getZ(), 25, 0.5, 1, 0.5, 0.25f);
                }
            }
        }
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "pat_health");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ARMOR, "pat_armor");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "pat_strength");
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_POWER, "pat_magic");
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_COOLDOWN, "pat_cooldown");
        Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_COST, "pat_cost");
        Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "pat_speed");
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
