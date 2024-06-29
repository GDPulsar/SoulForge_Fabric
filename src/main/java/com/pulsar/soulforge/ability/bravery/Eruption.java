package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.TeamUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;

public class Eruption extends AbilityBase {
    public final String name = "Eruption";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "eruption");
    public final int requiredLv = 7;
    public final int cost = 40;
    public final int cooldown = 300;
    public final AbilityType type = AbilityType.CAST;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
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
                        living.damage(player.getDamageSources().playerAttack(player), damage);
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
        return true;
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        return true;
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        return true;
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
            ServerWorld serverWorld = ((ServerPlayerEntity) player).getServerWorld();
            for (int i = 0; i < 64; i++) {
                Vec3d particlePos = new Vec3d(Math.sin(i * Math.PI / 32), 0f, Math.cos(i * Math.PI / 32)).multiply(aoeDist);
                serverWorld.spawnParticles((ServerPlayerEntity) player, new DustParticleEffect(Vec3d.unpackRgb(0xFF8800).toVector3f(), 1f), true, particlePos.x + centerPos.x, centerPos.y + 0.6f, particlePos.z + centerPos.z, 1, 0, 0, 0, 0);
            }
        }
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
        return new Eruption();
    }
}
