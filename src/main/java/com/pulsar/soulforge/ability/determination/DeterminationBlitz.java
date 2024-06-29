package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.ability.bravery.BraveryBoost;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.util.TeamUtils;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.block.SideShapeType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import org.apache.http.util.EntityUtils;

import java.util.Objects;

public class DeterminationBlitz extends AbilityBase {
    public final String name = "Determination Blitz";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "determination_blitz");
    public final int requiredLv = 5;
    public final int cost = 33;
    public final int cooldown = 300;
    public final AbilityType type = AbilityType.CAST;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        Vec3d lookPos;
        HitResult hit = player.raycast(15f, 0f, true);
        if (hit != null) lookPos = hit.getPos();
        else lookPos = new Vec3d(player.getRotationVector().x, 0f, player.getRotationVector().z).normalize().multiply(15f).add(player.getPos());
        BlockHitResult target = player.getWorld().raycast(new RaycastContext(lookPos, lookPos.subtract(0, 100, 0), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, player));
        if (target != null) {
            Vec3d end;
            if (player.getWorld().getBlockState(target.getBlockPos()).isSideSolid(player.getWorld(), target.getBlockPos(), Direction.UP, SideShapeType.CENTER)) end = target.getBlockPos().toCenterPos().add(0, player.getWorld().getBlockState(target.getBlockPos()).getCollisionShape(player.getWorld(), target.getBlockPos()).getMax(Direction.Axis.Y), 0);
            else end = target.getBlockPos().toCenterPos();
            Vec3d start = player.getPos();
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            for (int i = 0; i < 30; i++) {
                Vec3d pos = start.lerp(end, i / 30f);
                Box box = new Box(pos.subtract(0.5f, 0.5f, 0.5f), pos.add(0.5f, 0.5f, 0.5f));
                for (Entity entity : player.getEntityWorld().getOtherEntities(player, box)) {
                    if (entity instanceof PlayerEntity targetPlayer) {
                        if (!TeamUtils.canDamagePlayer(player.getServer(), player, targetPlayer)) continue;
                    }
                    if (entity instanceof LivingEntity living) {
                        living.damage(SoulForgeDamageTypes.of(player.getWorld(), SoulForgeDamageTypes.ABILITY_DAMAGE_TYPE), 5f+playerSoul.getEffectiveLV()*0.66f);
                    }
                }
                ServerWorld serverWorld = Objects.requireNonNull(player.getServer()).getWorld(player.getWorld().getRegistryKey());
                serverWorld.spawnParticles(new DustParticleEffect(DustParticleEffect.RED, 1f), pos.x, pos.y+0.5f, pos.z, 5, 0.2f, 0.5f, 0.2f, 0);
            }
            player.teleport(end.x, end.y - 0.5f, end.z);
            return true;
        }
        return false;
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
        Vec3d lookPos;
        HitResult hit = player.raycast(15f, 0f, true);
        if (hit != null) lookPos = hit.getPos();
        else lookPos = new Vec3d(player.getRotationVector().x, 0f, player.getRotationVector().z).normalize().multiply(15f).add(player.getPos());
        BlockHitResult target = player.getWorld().raycast(new RaycastContext(lookPos, lookPos.subtract(0, 100, 0), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, player));
        if (target != null) {
            Vec3d end;
            if (player.getWorld().getBlockState(target.getBlockPos()).isSideSolid(player.getWorld(), target.getBlockPos(), Direction.UP, SideShapeType.CENTER)) end = target.getBlockPos().toCenterPos().add(0, player.getWorld().getBlockState(target.getBlockPos()).getCollisionShape(player.getWorld(), target.getBlockPos()).getMax(Direction.Axis.Y), 0);
            else end = target.getBlockPos().toCenterPos();
            ServerWorld serverWorld = ((ServerPlayerEntity)player).getServerWorld();
            serverWorld.spawnParticles(new DustParticleEffect(DustParticleEffect.RED, 1f), end.x, end.y+0.5f, end.z, 5, 0.2f, 0.5f, 0.2f, 0);
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
        return new DeterminationBlitz();
    }
}
