package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StatusInversion extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        EntityHitResult hit = Utils.getFocussedEntity(player, 32f);
        if (hit != null) {
            if (hit.getEntity() instanceof LivingEntity target) {
                Collection<StatusEffectInstance> effects = target.getStatusEffects();
                List<StatusEffectInstance> newEffects = new ArrayList<>();
                if (target.getFireTicks() > 0) {
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, (int)(target.getFireTicks()*0.6f), 0));
                }
                if (target instanceof PlayerEntity targetPlayer) {
                    SoulComponent playerSoul = SoulForge.getPlayerSoul(targetPlayer);
                    if (playerSoul.hasValue("antiheal") && playerSoul.hasValue("antihealDuration")) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, (int) (playerSoul.getValue("antihealDuration")), (int) (playerSoul.getValue("antiheal") * 0.06f)));
                    }
                }
                for (StatusEffectInstance effect : effects) {
                    if (effect.getEffectType() == StatusEffects.SPEED) newEffects.add(new StatusEffectInstance(StatusEffects.SLOWNESS, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.SLOWNESS) newEffects.add(new StatusEffectInstance(StatusEffects.SPEED, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.DOLPHINS_GRACE) newEffects.add(new StatusEffectInstance(StatusEffects.SLOWNESS, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.STRENGTH) newEffects.add(new StatusEffectInstance(StatusEffects.WEAKNESS, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.WEAKNESS) newEffects.add(new StatusEffectInstance(StatusEffects.STRENGTH, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.REGENERATION) newEffects.add(new StatusEffectInstance(StatusEffects.POISON, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.POISON) newEffects.add(new StatusEffectInstance(StatusEffects.REGENERATION, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.WITHER) newEffects.add(new StatusEffectInstance(StatusEffects.REGENERATION, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == SoulForgeEffects.VULNERABILITY) newEffects.add(new StatusEffectInstance(StatusEffects.RESISTANCE, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.RESISTANCE) newEffects.add(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.FIRE_RESISTANCE) player.setFireTicks((int) (effect.getDuration()*0.6f));
                    if (effect.getEffectType() == StatusEffects.LUCK) newEffects.add(new StatusEffectInstance(StatusEffects.UNLUCK, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.UNLUCK) newEffects.add(new StatusEffectInstance(StatusEffects.LUCK, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.HUNGER) newEffects.add(new StatusEffectInstance(StatusEffects.SATURATION, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.SATURATION) newEffects.add(new StatusEffectInstance(StatusEffects.HUNGER, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.HASTE) newEffects.add(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.MINING_FATIGUE) newEffects.add(new StatusEffectInstance(StatusEffects.HASTE, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.GLOWING) newEffects.add(new StatusEffectInstance(StatusEffects.INVISIBILITY, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.INVISIBILITY) newEffects.add(new StatusEffectInstance(StatusEffects.GLOWING, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.JUMP_BOOST) newEffects.add(new StatusEffectInstance(StatusEffects.SLOW_FALLING, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.SLOW_FALLING) newEffects.add(new StatusEffectInstance(StatusEffects.LEVITATION, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.LEVITATION) newEffects.add(new StatusEffectInstance(StatusEffects.SLOW_FALLING, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.NIGHT_VISION) newEffects.add(new StatusEffectInstance(StatusEffects.DARKNESS, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.DARKNESS) newEffects.add(new StatusEffectInstance(StatusEffects.NIGHT_VISION, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == StatusEffects.BLINDNESS) newEffects.add(new StatusEffectInstance(StatusEffects.NIGHT_VISION, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == SoulForgeEffects.CRUSHED) newEffects.add(new StatusEffectInstance(StatusEffects.RESISTANCE, effect.getDuration(), 0));
                    if (effect.getEffectType() == SoulForgeEffects.FROSTBITE) newEffects.add(new StatusEffectInstance(SoulForgeEffects.FROSTBURN, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == SoulForgeEffects.FROSTBURN) newEffects.add(new StatusEffectInstance(SoulForgeEffects.FROSTBITE, effect.getDuration(), MathHelper.floor(effect.getAmplifier()*0.6f)));
                    if (effect.getEffectType() == SoulForgeEffects.FROSTBURN) player.setFireTicks((int) (effect.getDuration()*0.6f));
                    if (effect.getEffectType() == SoulForgeEffects.MANA_OVERLOAD) newEffects.add(effect);
                    if (effect.getEffectType() == SoulForgeEffects.SNOWED_VISION) newEffects.add(effect);
                    if (effect.getEffectType() == SoulForgeEffects.CREATIVE_ZONE) newEffects.add(effect);
                    if (effect.getEffectType() == SoulForgeEffects.VALIANT_HEART) newEffects.add(effect);
                }
                target.clearStatusEffects();
                for (StatusEffectInstance effect : newEffects) {
                    target.addStatusEffect(effect);
                }
                Vec3d centerPos = target.getPos().add(0, 1, 0);
                for (int i = 0; i < 16; i++) {
                    Vec3d particlePos = new Vec3d(Math.sin(i*Math.PI/8), 0f, Math.cos(i*Math.PI/8));
                    player.getServerWorld().spawnParticles(player, new DustParticleEffect(Vec3d.unpackRgb(0x00FFFF).toVector3f(), 1f), true, particlePos.x + centerPos.x, centerPos.y + 0.3f, particlePos.z + centerPos.z, 1, 0, 0, 0, 0);
                    player.getServerWorld().spawnParticles(player, new DustParticleEffect(Vec3d.unpackRgb(0x00FF0).toVector3f(), 1f), true, particlePos.x + centerPos.x, centerPos.y, particlePos.z + centerPos.z, 1, 0, 0, 0, 0);
                    player.getServerWorld().spawnParticles(player, new DustParticleEffect(Vec3d.unpackRgb(0x00FFFF).toVector3f(), 1f), true, particlePos.x + centerPos.x, centerPos.y - 0.3f, particlePos.z + centerPos.z, 1, 0, 0, 0, 0);
                }
            }
        }
        return super.cast(player);
    }

    public int getLV() { return 12; }

    public int getCost() { return 40; }

    public int getCooldown() { return 200; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new StatusInversion();
    }
}
