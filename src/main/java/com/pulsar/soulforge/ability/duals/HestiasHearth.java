package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class HestiasHearth extends ToggleableAbilityBase {
    public Vec3d hestiaPos = null;
    public int charge = 0;
    public int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (!getActive()) {
            hestiaPos = player.getPos();
            charge = 15;
            timer = 40;
            super.cast(player);
            return true;
        } else {
            if (playerSoul.getMagic() < 5f) return false;
            if (charge < 100) {
                if (player.getPos().withAxis(Direction.Axis.Y, 0).distanceTo(hestiaPos.withAxis(Direction.Axis.Y, 0)) < 8f) {
                    charge += 2;
                } else {
                    return false;
                }
            }
            playerSoul.setMagic(playerSoul.getMagic() - 5f);
            playerSoul.resetLastCastTime();
            return false;
        }
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (hestiaPos != null && getActive()) {
            ServerWorld serverWorld = player.getServerWorld();
            float phiStep = (float) (Math.PI / 16);
            for (int i = 0; i < 32; i++) {
                float phi = i * phiStep;
                float x = (float)Math.cos(phi);
                float z = (float)Math.sin(phi);
                Vector3f particlePos = new Vector3f(x, 0f, z).normalize().mul(8);
                Vector3f color = Vec3d.unpackRgb(0xFF7700).toVector3f();
                if (phi % 4 <= 1) color = Vec3d.unpackRgb(0x00FF00).toVector3f();
                serverWorld.spawnParticles(player, new DustParticleEffect(color, 1f), true, particlePos.x + hestiaPos.x, particlePos.y + hestiaPos.y, particlePos.z + hestiaPos.z, 1, 0, 0, 0, 0);
            }
            serverWorld.spawnParticles(player, ParticleTypes.FLAME, true, hestiaPos.x, hestiaPos.y+MathHelper.ceil(charge/25f), hestiaPos.z, MathHelper.ceil(charge/25f)*25, MathHelper.ceil(charge/25f)/3f, MathHelper.ceil(charge/25f)/3f, MathHelper.ceil(charge/25f)/3f, 0);
        }
        timer--;
        if (timer <= 0) {
            charge--;
            timer = 40;
        }
        if (charge > 0 && charge <= 55) {
            if (timer >= 40) {
                for (Entity entity : player.getEntityWorld().getOtherEntities(null, Box.of(hestiaPos, 16, 16, 16))) {
                    if (entity.getPos().withAxis(Direction.Axis.Y, 0).distanceTo(hestiaPos.withAxis(Direction.Axis.Y, 0)) > 8) continue;
                    if (entity instanceof LivingEntity living) {
                        if (entity instanceof PlayerEntity targetPlayer) {
                            if (!TeamUtils.canHealPlayer(player.getServer(), player, targetPlayer)) continue;
                        }
                        living.heal(1f);
                        if (charge >= 35) {
                            for (StatusEffectInstance effect : List.copyOf(living.getStatusEffects())) {
                                if (effect.getEffectType() == SoulForgeEffects.MANA_SICKNESS) continue;
                                living.removeStatusEffect(effect.getEffectType());
                            }
                        }
                    }
                }
            }
        } else if (charge > 55) {
            for (Entity entity : player.getEntityWorld().getOtherEntities(player, Box.of(hestiaPos, 16, 16, 16))) {
                if (entity.getPos().withAxis(Direction.Axis.Y, 0).distanceTo(hestiaPos.withAxis(Direction.Axis.Y, 0)) > 8) continue;
                if (entity.getFireTicks() < 60) {
                    entity.setFireTicks(70);
                }
                if (entity instanceof LivingEntity living) {
                    if (entity instanceof PlayerEntity targetPlayer) {
                        if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
                    }
                    if ((charge < 70 && timer >= 40) || (charge < 85 && timer % 20 == 0) || (charge >= 85 && timer % 10 == 0)) {
                        living.damage(SoulForgeDamageTypes.of(player, SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), 1f);
                    }
                }
            }
        }
        return charge <= 0 || charge >= 100;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        if (charge >= 100) {
            player.getWorld().createExplosion(player, hestiaPos.x, hestiaPos.y, hestiaPos.z, 5f, World.ExplosionSourceType.NONE);
            if (player.getPos().distanceTo(hestiaPos) < charge/10f) {
                player.heal(player.getMaxHealth());
            }
        }
        setActive(false);
        hestiaPos = null;
        charge = 0;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.setCooldown(this, 1800);
        return super.end(player);
    }

    @Override
    public void displayTick(PlayerEntity player) {
        if (getActive()) {
            player.sendMessage(Text.literal(String.valueOf(charge)).append("%").formatted(Formatting.GOLD), true);
        }
    }

    public String getName() { return "Hestia's Hearth"; }

    public Identifier getID() { return new Identifier(SoulForge.MOD_ID, "hestias_hearth"); }

    public int getLV() { return 10; }

    public int getCost() { return 5; }

    public int getCooldown() { return 1800; }

    @Override
    public AbilityBase getInstance() {
        return new HestiasHearth();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        nbt.putInt("charge", charge);
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        super.saveNbt(nbt);
        nbt.putInt("charge", charge);
        return nbt;
    }
}
