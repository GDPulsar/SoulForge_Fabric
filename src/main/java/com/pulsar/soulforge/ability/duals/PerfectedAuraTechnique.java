package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class PerfectedAuraTechnique extends ToggleableAbilityBase {
    public final String name = "Perfected Aura Technique";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "perfected_aura_technique");
    public final int requiredLv = 15;
    public final int cost = 100;
    public final int cooldown = 0;

    public boolean fullPower = false;
    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        toggleActive();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (getActive()) {
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
            Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "pat_health");
            Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ARMOR, "pat_armor");
            Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "pat_strength");
            Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_POWER, "pat_magic");
            Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_COOLDOWN, "pat_cooldown");
            Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_COST, "pat_cost");
            Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "pat_speed");
            EntityAttributeModifier healthModifier = new EntityAttributeModifier("pat_health", 2, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            EntityAttributeModifier strengthModifier = new EntityAttributeModifier("pat_strength", 1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            EntityAttributeModifier magicModifier = new EntityAttributeModifier("pat_magic", 1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            EntityAttributeModifier cooldownModifier = new EntityAttributeModifier("pat_cooldown", -0.5, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            EntityAttributeModifier costModifier = new EntityAttributeModifier("pat_cost", -0.5, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            EntityAttributeModifier speedModifier = new EntityAttributeModifier("pat_speed", 0.15, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(healthModifier);
            player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(strengthModifier);
            player.getAttributeInstance(SoulForgeAttributes.MAGIC_POWER).addPersistentModifier(magicModifier);
            player.getAttributeInstance(SoulForgeAttributes.MAGIC_COOLDOWN).addPersistentModifier(cooldownModifier);
            player.getAttributeInstance(SoulForgeAttributes.MAGIC_COST).addPersistentModifier(costModifier);
            player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(speedModifier);
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
                Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "pat_health");
                Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ARMOR, "pat_armor");
                Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "pat_strength");
                Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_POWER, "pat_magic");
                Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_COOLDOWN, "pat_cooldown");
                Utils.clearModifiersByName(player, SoulForgeAttributes.MAGIC_COST, "pat_cost");
                Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, "pat_speed");
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
            Utils.clearModifiersByName(player, EntityAttributes.GENERIC_MAX_HEALTH, "pat_health");
            Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ARMOR, "pat_armor");
            Utils.clearModifiersByName(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, "pat_strength");
            EntityAttributeModifier healthModifier = new EntityAttributeModifier("pat_health", playerSoul.getEffectiveLV() / 2f, EntityAttributeModifier.Operation.ADDITION);
            EntityAttributeModifier strengthModifier = new EntityAttributeModifier("pat_strength", playerSoul.getEffectiveLV() * 0.0175f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            EntityAttributeModifier armorModifier = new EntityAttributeModifier("pat_armor", playerSoul.getEffectiveLV() / 10f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(healthModifier);
            player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addPersistentModifier(armorModifier);
            player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(strengthModifier);
        }
        return !getActive();
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
        return true;
    }

    public String getName() { return name; }

    public Text getLocalizedText() { return Text.translatable("ability."+id.getPath()+".name"); }

    public Identifier getID() { return id; }

    public String getTooltip() { return Text.translatable("ability."+id.getPath()+".tooltip").getString(); }

    public int getLV() { return requiredLv; }

    public int getCost() { return cost; }

    public int getCooldown() { return cooldown; }

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
    }
}
