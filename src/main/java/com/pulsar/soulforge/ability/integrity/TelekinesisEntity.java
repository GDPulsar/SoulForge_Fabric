package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class TelekinesisEntity extends ToggleableAbilityBase {
    public final String name = "Telekinesis: Entity";
    public final Identifier id = Identifier.of(SoulForge.MOD_ID, "telekinesis_entity");
    public final int requiredLv = 3;
    public final int cost = 25;
    public final int cooldown = 900;

    private LivingEntity target = null;
    private int timer = 0;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        super.cast(player);
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (target == null) {
            EntityHitResult hit = Utils.getFocussedEntity(player, 6);
            if (hit != null && hit.getEntity() instanceof LivingEntity living) {
                HitResult hit2 = player.getWorld().raycast(new RaycastContext(player.getPos(), living.getPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
                if (hit2.getPos().distanceTo(living.getPos()) > 0.2f) return false;
                target = living;
                timer = playerSoul.getEffectiveLV()*20;
                player.getWorld().playSoundFromEntity(null, player, SoulForgeSounds.SOUL_GRAB_EVENT, SoundCategory.PLAYERS, 1f, 1f);
                for (int i = 0; i < 10; i++) {
                    float x = MathHelper.sin((float)(i/5*Math.PI));
                    float z = MathHelper.cos((float)(i/5*Math.PI));
                    player.getServerWorld().spawnParticles(
                            new DustParticleEffect(Vec3d.unpackRgb(0x0000FF).toVector3f(), 1f),
                            target.getX()+x, target.getY(), target.getZ()+z, 1, 0, 0.2, 0, 0);
                }
            }
        } else {
            target.setVelocity(target.getPos().subtract(target.prevX, target.prevY, target.prevZ));
            target.velocityModified = true;
            target = null;
        }
        return isActive();
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (target != null) {
            Vec3d tpPos;
            HitResult hit = player.raycast(4f, 0, false);
            if (hit != null) {
                if (hit.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult)hit;
                    tpPos = blockHitResult.getBlockPos().toCenterPos().add(Vec3d.of(blockHitResult.getSide().getVector()));
                } else {
                    tpPos = hit.getPos();
                }
            }
            else tpPos = player.getRotationVector().multiply(4f).add(player.getEyePos());
            if (target.getPos().add(player.getVelocity()).distanceTo(tpPos) > 7.5f) return true;
            target.setPos(tpPos.x, tpPos.y, tpPos.z);
            target.setVelocity(Vec3d.ZERO);
            target.velocityModified = true;
            target.fallDistance = 0f;
            if (target.isDead()) target = null;
        }
        timer--;
        return target == null || timer <= 0 || !isActive();
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        target = null;
        setActive(false);
        return super.end(player);
    }

    public int getLV() { return 3; }
    public int getCost() { return 25; }
    public int getCooldown() { return 900; }

    @Override
    public AbilityBase getInstance() {
        return new TelekinesisEntity();
    }
}
