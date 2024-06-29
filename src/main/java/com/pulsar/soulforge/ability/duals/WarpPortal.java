package com.pulsar.soulforge.ability.duals;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.damage_type.SoulForgeDamageTypes;
import com.pulsar.soulforge.entity.WormholeEntity;
import net.minecraft.block.SideShapeType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Objects;

public class WarpPortal extends AbilityBase {
    public final String name = "Warp Portal";
    public final Identifier id = new Identifier(SoulForge.MOD_ID, "warp_portal");
    public final int requiredLv = 10;
    public final int cost = 30;
    public final int cooldown = 200;
    public final AbilityType type = AbilityType.CAST;

    @Override
    public boolean cast(ServerPlayerEntity player) {
        Vec3d lookPos;
        HitResult hit = player.raycast(75f, 0f, true);
        if (hit != null) lookPos = hit.getPos();
        else lookPos = player.getRotationVector().multiply(75f).add(player.getPos());
        BlockHitResult target = player.getWorld().raycast(new RaycastContext(lookPos, lookPos.subtract(0, 100, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, player));
        if (target != null) {
            Vec3d direction = player.getRotationVector().withAxis(Direction.Axis.Y, 0);
            Vec3d end = target.getBlockPos().toCenterPos().add(0, 1, 0);
            Vec3d start = player.getPos().add(direction.normalize().multiply(2.5f)).add(0, 1, 0);
            ServerWorld serverWorld = player.getServerWorld();
            WormholeEntity startWormhole = new WormholeEntity(serverWorld, start, serverWorld, end.add(direction.multiply(1.5f)), player.getRotationVector().withAxis(Direction.Axis.Y, 0));
            serverWorld.spawnEntity(startWormhole);
            WormholeEntity endWormhole = new WormholeEntity(serverWorld, end, serverWorld, start.add(direction.multiply(-1.5f)), player.getRotationVector().withAxis(Direction.Axis.Y, 0).negate());
            serverWorld.spawnEntity(endWormhole);
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
        return new WarpPortal();
    }
}
