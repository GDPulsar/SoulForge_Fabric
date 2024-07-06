package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.SnowgraveProjectile;
import com.pulsar.soulforge.entity.SoulForgeEntities;
import com.pulsar.soulforge.networking.SoulForgeNetworking;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Snowgrave extends AbilityBase {
    private final EntityAttributeModifier modifier = new EntityAttributeModifier("snowgrave", -1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    private BlockPos target;
    private int timer = 0;
    private List<SnowgraveProjectile> projectiles = new ArrayList<>();
    private Vec3d castPos;
    private Vec3d playerPos;
    private Vec3d particleVel;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        HitResult result = player.raycast(50, 1f, false);
        if (result != null) {
            if (result.squaredDistanceTo(player) <= 2500f) {
                SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                playerSoul.addTag("preventJump");
                playerSoul.addTag("preventMove");
                playerSoul.addTag("forcedThirdPerson");
                if (!player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).hasModifier(modifier))
                    player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addTemporaryModifier(modifier);
                target = new BlockPos(MathHelper.floor(result.getPos().x), MathHelper.floor(result.getPos().y), MathHelper.floor(result.getPos().z));
                timer = 0;
                castPos = player.getPos();
                playerPos = castPos;
                particleVel = new Vec3d(Math.random() * 5 - 2.5, Math.random() - 2, Math.random() * 5 - 2.5);
                player.getWorld().playSoundFromEntity(null, player, SoulForgeSounds.SNOWGRAVE_EVENT, SoundCategory.MASTER, 3f, 1f);
                if (player.getServer() != null) {
                    PacketByteBuf buf = PacketByteBufs.create().writeUuid(player.getUuid()).writeString("snowgrave");
                    buf.writeBoolean(false);
                    SoulForgeNetworking.broadcast(null, player.getServer(), SoulForgeNetworking.PERFORM_ANIMATION, buf);
                }
                return super.cast(player);
            }
        }
        return false;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (target == null || castPos == null) return true;
        timer++;
        ServerWorld serverWorld = player.getServerWorld();
        assert serverWorld != null;
        for (int i = 0; i < (timer >= 200 && timer < 400 ? 250 : 100); i++) {
            float x = (float) (Math.random() * 60 - 30);
            float y = (float) (Math.random() * 25);
            float z = (float) (Math.random() * 60 - 30);
            serverWorld.addParticle(ParticleTypes.SNOWFLAKE, target.getX()+x, target.getY()+y, target.getZ()+z, -1f + Math.random()*0.2f, -0.2f - Math.random()*0.1f, -0.5f - Math.random()*0.2f);
        }
        particleVel = new Vec3d(MathHelper.clamp(particleVel.getX(), -5, 5), MathHelper.clamp(particleVel.getY(), -3, -1), MathHelper.clamp(particleVel.getZ(), -5, 5));
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        boolean frostburn = playerSoul.getTraits().contains(Traits.bravery) && playerSoul.getTraits().contains(Traits.patience);
        for (Entity entity : serverWorld.iterateEntities()) {
            if (entity instanceof LivingEntity targeted && entity != player) {
                if (targeted instanceof PlayerEntity targetPlayer) {
                    if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
                }
                if (timer >= 200) {
                    if (targeted.getPos().distanceTo(target.toCenterPos()) <= 20f) {
                        targeted.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1, 6));
                        targeted.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 1, 4));
                        targeted.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.VULNERABILITY, 1, 4));
                        targeted.addStatusEffect(new StatusEffectInstance(
                                frostburn ? SoulForgeEffects.FROSTBURN : SoulForgeEffects.FROSTBITE,
                                100, 0));
                    }
                    if (targeted.getPos().distanceTo(target.toCenterPos()) <= 10f) {
                        if (targeted instanceof PlayerEntity) {
                            SoulComponent targetSoul = SoulForge.getPlayerSoul((PlayerEntity)targeted);
                            targetSoul.addTag("immobile");
                        }
                        targeted.setVelocity(0, 0, 0);
                        targeted.velocityModified = true;
                    } else if (targeted.getPos().distanceTo(target.toCenterPos()) <= 11.5f){
                        if (targeted instanceof PlayerEntity) {
                            SoulComponent targetSoul = SoulForge.getPlayerSoul((PlayerEntity)targeted);
                            targetSoul.removeTag("immobile");
                        }
                    }
                }
            }
        }
        if (timer < 200) {
            // phase 1
            if (timer >= 100) {
                Vec3d direction = castPos.subtract(target.toCenterPos());
                direction = new Vec3d(direction.x, 0f, direction.z).normalize();
                Vec3d floatToLocation = new Vec3d(direction.x*15f, 7f, direction.z*15f).add(target.toCenterPos());
                playerPos = castPos.lerp(floatToLocation, (double)(timer-100)/100D);
            }
        } else if (timer < 300) {
            // phase 2
            for (int x = -15; x < 16; x++) {
                for (int z = -15; z < 16; z++) {
                    float distance = (float) MathHelper.hypot(x, z);
                    if (distance < 15f) {
                        if (Math.random() <= 0.05f * MathHelper.clamp((15f - distance)/2f, 0, 1)) {
                            try {
                                BlockPos snowPos = Utils.getTopBlock(player.getServer(), player.getWorld(), target.getX() + x, target.getZ() + z);
                                BlockState state = player.getWorld().getBlockState(snowPos);
                                if (state.isOf(Blocks.SNOW)) {
                                    int layers = state.get(Properties.LAYERS);
                                    if (layers == 7) {
                                        player.getWorld().setBlockState(snowPos, Blocks.SNOW_BLOCK.getDefaultState());
                                    } else {
                                        player.getWorld().setBlockState(snowPos, state.with(Properties.LAYERS, Math.min(8, layers + 1)));
                                    }
                                } else {
                                    if (player.getWorld().isTopSolid(snowPos.add(0, -1, 0), player)) {
                                        player.getWorld().setBlockState(snowPos, Blocks.SNOW.getDefaultState().with(Properties.LAYERS, 1));
                                    }
                                    if (player.getWorld().getBlockState(snowPos.add(0, -1, 0)).isOf(Blocks.WATER)) {
                                        player.getWorld().setBlockState(snowPos.add(0, -1, 0), Blocks.ICE.getDefaultState());
                                    }
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }
        } else if (timer < 400) {
            if (timer == 300) player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_SICKNESS, 2570, 2));
            // phase 3
            if (timer <= 325) {
                for (SnowgraveProjectile projectile : projectiles) {
                    projectile.setPosition(projectile.getPos().add(0, 1.2, 0));
                }
                if (timer % 5 == 0) projectiles.add(SoulForgeEntities.SNOWGRAVE_PROJECTILE_TYPE.spawn(serverWorld, target.add(0, -6, 0), SpawnReason.EVENT));
            }
            for (Entity entity : serverWorld.iterateEntities()) {
                if (entity instanceof LivingEntity living && entity != player) {
                    if (living instanceof PlayerEntity targetPlayer) {
                        if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
                    }
                    if (living.getPos().distanceTo(target.toCenterPos()) < 7f) {
                        living.damage(SoulForgeDamageTypes.of(player.getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), 1f);
                        living.timeUntilRegen = 0;
                        living.hurtTime = 0;
                    }
                }
            }
        } else if (timer < 470) {
            // phase 4
            for (SnowgraveProjectile projectile : projectiles) {
                projectile.kill();
            }
            for (Entity entity : serverWorld.iterateEntities()) {
                if (entity instanceof PlayerEntity targetPlayer && entity != player) {
                    if (targetPlayer.getPos().distanceTo(target.toCenterPos()) < 14f) {
                        SoulComponent targetSoul = SoulForge.getPlayerSoul(targetPlayer);
                        targetSoul.removeTag("immobile");
                    }
                }
            }
            projectiles.clear();
            if (timer == 400) castPos = player.getPos();
            if (timer <= 420) {
                try {
                    Vec3d bottom = Utils.getTopBlock(player.getServer(), player.getWorld(), player.getBlockX(), player.getBlockZ()).toCenterPos();
                    playerPos = castPos.lerp(bottom, (double) (timer - 400) / 20D);
                    player.fallDistance = 0f;
                    player.startFallFlying();
                } catch (Exception ignored) {}
            }
        }
        if (timer <= 420) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(true); buf.writeVector3f(playerPos.toVector3f());
            buf.writeBoolean(true); buf.writeVector3f(new Vec3d(0, 0, 0).toVector3f());
            buf.writeBoolean(false);
            ServerPlayNetworking.send(player, SoulForgeNetworking.POSITION_VELOCITY, buf);
        }
        return timer >= 470;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.removeTag("preventJump");
        playerSoul.removeTag("preventMove");
        playerSoul.removeTag("forcedThirdPerson");
        player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).removeModifier(modifier.getId());
        return super.end(player);
    }

    @Override
    public void displayTick(PlayerEntity player) {
        HitResult result = player.raycast(50, 1f, false);
        if (result != null) {
            if (result.squaredDistanceTo(player) <= 2500f) {
                BlockPos target = new BlockPos(MathHelper.floor(result.getPos().x), MathHelper.floor(result.getPos().y), MathHelper.floor(result.getPos().z));
                Vec3d centerPos = target.toCenterPos();
                for (int i = 0; i < 64; i++) {
                    Vec3d particlePos = new Vec3d(Math.sin(i*Math.PI/32), 0f, Math.cos(i*Math.PI/32)).multiply(7f);
                    player.getWorld().addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00FFFF).toVector3f(), 1f), particlePos.x + centerPos.x, centerPos.y + 0.6f, particlePos.z + centerPos.z, 0, 0, 0);
                    particlePos = new Vec3d(Math.sin(i*Math.PI/32), 0f, Math.cos(i*Math.PI/32)).multiply(15f);
                    player.getWorld().addParticle(new DustParticleEffect(Vec3d.unpackRgb(0x00FFFF).toVector3f(), 1f), particlePos.x + centerPos.x, centerPos.y + 0.6f, particlePos.z + centerPos.z, 0, 0, 0);
                }
            }
        }
    }

    public int getLV() { return 20; }

    public int getCost() { return 100; }

    public int getCooldown() { return 9600; }

    public AbilityType getType() { return AbilityType.SPECIAL; }

    @Override
    public AbilityBase getInstance() {
        return new Snowgrave();
    }
}
