package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class Eruption extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        float totalDamage = 0f;
        float distanceForward = 7f + MathHelper.floor(playerSoul.getEffectiveLV()/4f);
        float aoeDist = 2f + MathHelper.floor(playerSoul.getEffectiveLV()/8f);
        float damage = 5f + MathHelper.floor(playerSoul.getEffectiveLV()/4f);
        Vec3d lookPos;
        HitResult hit = player.raycast(15f, 0f, true);
        if (hit != null) lookPos = hit.getPos();
        else lookPos = new Vec3d(player.getRotationVector().x, 0f, player.getRotationVector().z).normalize().multiply(distanceForward).add(player.getPos());
        BlockHitResult target = player.getWorld().raycast(new RaycastContext(lookPos, lookPos.subtract(0, 100, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, player));
        if (target != null) {
            Vec3d centerPoint = target.getBlockPos().toCenterPos();
            for (Entity entity : player.getEntityWorld().getOtherEntities(player, Box.of(centerPoint, aoeDist * 2f, aoeDist * 2f, aoeDist * 2f))) {
                if (entity instanceof LivingEntity living) {
                    if (entity instanceof PlayerEntity targetPlayer) {
                        if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
                    }
                    float dist = (float) living.getPos().distanceTo(centerPoint);
                    if (dist <= aoeDist) {
                        Vec3d launchDir = centerPoint.subtract(living.getPos()).withAxis(Direction.Axis.Y, 0).normalize().multiply(dist / 3f);
                        if (living.damage(SoulForgeDamageTypes.of(player, SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), damage)) {
                            totalDamage += damage;
                        }
                        living.setVelocity(launchDir.add(0f, playerSoul.getEffectiveLV()/10f, 0f));
                    }
                }
            }
            ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, player.getSteppingBlockState());
            for (int i = 0; i < 50; i++) {
                Vec3d pos = centerPoint.add(new Vec3d(Math.random() * aoeDist - aoeDist / 2f, Math.random() * 0.1f, Math.random() * aoeDist - aoeDist / 2f));
                while (pos.distanceTo(centerPoint) > aoeDist) pos = centerPoint.add(new Vec3d(Math.random() * 3f - 1.5f, Math.random() * 0.1f, Math.random() * 3f - 1.5f));
                player.getServerWorld().spawnParticles(particle,
                        pos.x, pos.y, pos.z, 1, 0, 0, 0, 0.5);
            }
            player.getWorld().playSound(null, centerPoint.x, centerPoint.y, centerPoint.z, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.MASTER, 2.5f, 1f);
        }
        playerSoul.setStyle(playerSoul.getStyle() + (int)totalDamage);
        return super.cast(player);
    }

    @Override
    public void displayTick(PlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        float distanceForward = 7f + MathHelper.floor(playerSoul.getEffectiveLV()/4f);
        float aoeDist = 2f + MathHelper.floor(playerSoul.getEffectiveLV()/8f);
        Vec3d lookPos;
        HitResult hit = player.raycast(15f, 0f, true);
        if (hit != null) lookPos = hit.getPos();
        else lookPos = new Vec3d(player.getRotationVector().x, 0f, player.getRotationVector().z).normalize().multiply(distanceForward).add(player.getPos());
        BlockHitResult target = player.getWorld().raycast(new RaycastContext(lookPos, lookPos.subtract(0, 100, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, player));
        if (target != null) {
            Vec3d centerPos = target.getBlockPos().toCenterPos();
            for (int i = 0; i < 64; i++) {
                Vec3d particlePos = new Vec3d(Math.sin(i * Math.PI / 32), 0f, Math.cos(i * Math.PI / 32)).multiply(aoeDist);
                player.getWorld().addParticle(new DustParticleEffect(Vec3d.unpackRgb(0xFF8800).toVector3f(), 1f), particlePos.x + centerPos.x, centerPos.y + 0.6f, particlePos.z + centerPos.z, 0, 0, 0);
            }
        }
    }

    public int getLV() { return 7; }

    public int getCost() { return 40; }

    public int getCooldown() { return 300; }

    public AbilityType getType() { return AbilityType.CAST; }

    @Override
    public AbilityBase getInstance() {
        return new Eruption();
    }
}
