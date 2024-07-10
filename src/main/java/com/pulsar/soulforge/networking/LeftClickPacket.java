package com.pulsar.soulforge.networking;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.effects.SoulForgeEffects;
import com.pulsar.soulforge.entity.BlastEntity;
import com.pulsar.soulforge.entity.BouncingShieldEntity;
import com.pulsar.soulforge.entity.DeterminationArrowProjectile;
import com.pulsar.soulforge.entity.YoyoProjectile;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.devices.devices.JusticeGun;
import com.pulsar.soulforge.item.weapons.Gunblades;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.trait.Traits;
import com.pulsar.soulforge.util.Utils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.event.GameEvent;

import java.awt.*;

public class LeftClickPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (player.isUsingItem()) {
            ItemStack using = player.getActiveItem();
            if (playerSoul.getTraits().contains(Traits.kindness) && playerSoul.getTraits().contains(Traits.justice)) {
                boolean shieldExists = false;
                for (Entity entity : player.getEntityWorld().getEntitiesByClass(BouncingShieldEntity.class, Box.of(player.getPos(), 200, 200, 200), (shield) -> shield.owner == player)) {
                    if (entity instanceof BouncingShieldEntity shield) {
                        if (shield.owner == player) {
                            shieldExists = true;
                        }
                    }
                }
                if (!shieldExists && using.isOf(SoulForgeItems.KINDNESS_SHIELD)) {
                    BouncingShieldEntity shield = new BouncingShieldEntity(player, player.getEyePos(), player.getRotationVector().multiply(1.5f));
                    shield.setPosition(player.getEyePos());
                    shield.setPos(player.getEyePos());
                    shield.setVelocity(player.getRotationVector().multiply(1.5f));
                    player.getWorld().spawnEntity(shield);
                }
            } else {
                if (using.isOf(SoulForgeItems.KINDNESS_SHIELD) || using.isOf(SoulForgeItems.DETERMINATION_SHIELD)) {
                    if (playerSoul.hasValue("shieldBashCooldown")) {
                        if (playerSoul.getValue("shieldBashCooldown") > 0) return;
                    }
                    if (playerSoul.hasValue("shieldBash")) {
                        if (playerSoul.getValue("shieldBash") > 0) return;
                    }
                    Vec3d velAdd = player.getRotationVector();
                    velAdd = new Vec3d(velAdd.x, 0f, velAdd.z).normalize();
                    player.addVelocity(velAdd);
                    player.velocityModified = true;
                    playerSoul.setValue("shieldBash", 15);
                }
            }
            if (using.isOf(SoulForgeItems.GUNBLADES)) {
                ((Gunblades)using.getItem()).shoot(player, player.getWorld());
            }
        }
        ItemStack heldItem = player.getMainHandStack();
        if (heldItem != null) {
            if (heldItem.isOf(SoulForgeItems.BFRCMG)) {
                if (playerSoul.getMagic() >= 80f) {
                    playerSoul.setMagic(playerSoul.getMagic() - 80f);
                    playerSoul.resetLastCastTime();
                    HitResult hit = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getRotationVector().multiply(75f).add(player.getPos()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
                    Vec3d end = player.getRotationVector().multiply(75f);
                    if (hit != null) end = hit.getPos().subtract(Utils.getArmPosition(player));
                    BlastEntity blast = new BlastEntity(player.getWorld(), Utils.getArmPosition(player), player, 0.5f,
                            Vec3d.ZERO, end, playerSoul.getLV()/10f * heldItem.getNbt().getInt("heat"),
                            Color.YELLOW, true, 4);
                    blast.owner = player;
                    ServerWorld serverWorld = player.getServerWorld();
                    serverWorld.spawnEntity(blast);
                    serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, player.getPos(), GameEvent.Emitter.of(player));
                    serverWorld.playSoundFromEntity(null, player, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 0.2f, 1f);
                    player.addVelocity(player.getRotationVector().normalize().multiply(-1.5f));
                    player.velocityModified = true;
                    heldItem.getNbt().putInt("heat", 0);
                }
            }
            if (heldItem.isOf(SoulForgeItems.DETERMINATION_RAPIER)) {
                if (player.isSneaking()) {
                    if (playerSoul.getMagic() >= 5f) {
                        Vec3d end = player.getEyePos().add(player.getRotationVector().multiply(30f));
                        HitResult hit = player.getWorld().raycast(new RaycastContext(player.getEyePos(), player.getEyePos().add(player.getRotationVector().multiply(30f)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
                        if (hit != null) end = hit.getPos().subtract(Utils.getArmPosition(player));
                        BlastEntity blast = new BlastEntity(player.getWorld(), Utils.getArmPosition(player), player, 0.1f, Vec3d.ZERO, end, playerSoul.getLV() / 4f, Color.RED);
                        blast.owner = player;
                        ServerWorld serverWorld = (ServerWorld) player.getWorld();
                        serverWorld.spawnEntity(blast);
                        serverWorld.playSoundFromEntity(null, player, SoulForgeSounds.UT_BLASTER_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                        serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, player.getPos(), GameEvent.Emitter.of(player));
                        playerSoul.setMagic(playerSoul.getMagic() - 5f);
                        playerSoul.resetLastCastTime();
                    }
                }
            }
            if (heldItem.isOf(SoulForgeItems.DETERMINATION_BOW)) {
                if (player.isSneaking()) {
                    if (playerSoul.getMagic() >= 50f) {
                        Vec3d direction = new Vec3d(MathHelper.sin(-player.getYaw() * MathHelper.RADIANS_PER_DEGREE), 0f, MathHelper.cos(-player.getYaw() * MathHelper.RADIANS_PER_DEGREE));
                        Vec3d center = direction.multiply(15f).add(player.getPos());
                        for (int x = -10; x < 10; x++) {
                            for (int z = -10; z < 10; z++) {
                                if (Math.random() <= 0.25f) {
                                    int y = MathHelper.floor(Math.random() * 20f);
                                    Vec3d pos = center.add(x / 2f + Math.random(), y + 40f, z / 2f + Math.random());
                                    DeterminationArrowProjectile arrow = new DeterminationArrowProjectile(player.getWorld(), player);
                                    arrow.setPosition(pos);
                                    arrow.setVelocity(0f, 0f, 0f);
                                    player.getWorld().spawnEntity(arrow);
                                }
                            }
                        }
                        playerSoul.setMagic(playerSoul.getMagic() - 50f);
                        playerSoul.resetLastCastTime();
                        player.addStatusEffect(new StatusEffectInstance(SoulForgeEffects.MANA_SICKNESS, 600, 0));
                    }
                } else if (playerSoul.getMagic() >= 5f) {
                    for (DeterminationArrowProjectile arrow : player.getEntityWorld().getEntitiesByType(TypeFilter.instanceOf(DeterminationArrowProjectile.class),
                            Box.of(player.getPos(), 100, 100, 100), arrow -> true)) {
                        if (playerSoul.getMagic() < 5f) break;
                        LivingEntity target = player.getEntityWorld().getClosestEntity(player.getEntityWorld().getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), Box.of(arrow.getPos(), 20, 20, 20), entity -> true),
                                TargetPredicate.createAttackable(), player, arrow.getX(), arrow.getY(), arrow.getZ());
                        if (target != null) {
                            playerSoul.setMagic(playerSoul.getMagic()-5f);
                            playerSoul.resetLastCastTime();
                            arrow.setVelocity(target.getPos().subtract(arrow.getPos()).normalize().multiply(3f));
                        }
                    }
                }
            }
            if (heldItem.isOf(SoulForgeItems.COLOSSAL_CLAYMORE)) {
                if (player.getAttackCooldownProgress(0.5f) >= 1f && !player.isUsingItem()) {
                    for (LivingEntity target : Utils.getEntitiesInFrontOf(player, 2.5f, 3f, 1f, 2f)) {
                        target.damage(player.getDamageSources().playerAttack(player), playerSoul.getLV());
                    }
                    for (int i = 0; i < 3; i ++) {
                        ServerWorld serverWorld = player.getServerWorld();
                        Vec3d particlePos = new Vec3d(player.getRotationVector().x, 0.5f, player.getRotationVector().z).add(player.getPos());
                        serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, particlePos.x, particlePos.y+i/2f, particlePos.z, 1, 0, 0, 0, 0);
                    }
                }
            }
            if (heldItem.isOf(SoulForgeItems.JUSTICE_GUN)) {
                JusticeGun gun = (JusticeGun)heldItem.getItem();
                gun.reload(heldItem, player);
            }
            if (heldItem.isOf(SoulForgeItems.TRICK_ANCHOR)) {
                for (YoyoProjectile yoyo : player.getWorld().getEntitiesByClass(YoyoProjectile.class, Box.of(player.getPos(), 200, 200, 200), (entity) -> entity.getOwner() == player)) {
                    if (!yoyo.projectiles.isEmpty()) {
                        for (ProjectileEntity projectile : yoyo.projectiles) {
                            projectile.setPosition(yoyo.getPos());
                            projectile.setVelocity(player.getRotationVector().normalize().multiply(projectile.getVelocity().length()));
                            player.getWorld().spawnEntity(projectile);
                        }
                    } else {
                        yoyo.kill();
                        playerSoul.setValue("yoyoAoETimer", 15);
                    }
                }
            }
        }
    }
}
