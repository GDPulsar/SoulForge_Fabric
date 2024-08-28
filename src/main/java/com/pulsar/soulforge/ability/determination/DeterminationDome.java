package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.ToggleableAbilityBase;
import com.pulsar.soulforge.block.SoulForgeBlocks;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.entity.DomeEntity;
import com.pulsar.soulforge.entity.DomePart;
import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Objects;

public class DeterminationDome extends ToggleableAbilityBase {
    public float domeHealth = 200f;
    public DomeEntity entity;
    private BlockPos center = null;
    private final int domeRadius = 5;

    private float lengthSq(int x, int y, int z) {
        return (x*x + y*y + z*z);
    }

    @Override
    public boolean cast(ServerPlayerEntity player) {
        super.cast(player);
        if (getActive()) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            domeHealth = playerSoul.getEffectiveLV() * 10f;
            center = player.getBlockPos();
            player.getWorld().playSoundFromEntity(null, player, SoulForgeSounds.DR_RUDEBUSTER_SWING_EVENT, SoundCategory.PLAYERS, 150f, 1f);
            entity = new DomeEntity(player.getWorld(), player.getBlockPos().toCenterPos(), domeRadius, domeHealth);
            entity.setPosition(player.getBlockPos().toCenterPos().subtract(0, 0.5f, 0));
            double radius = domeRadius + 0.5;
            double radSq = radius * radius;
            double rad1Sq = (radius - 1.5) * (radius - 1.5);
            int ceilRad = MathHelper.ceil(radius);
            for (int x = 0; x <= ceilRad; x++) {
                for (int y = 0; y < ceilRad; y++) {
                    for (int z = 0; z <= ceilRad; z++) {
                        double distanceSq = lengthSq(x, y, z);
                        if (distanceSq > radSq) continue;
                        if (distanceSq < rad1Sq) continue;

                        placeDomeBlock(x, y, z, player);
                        placeDomeBlock(-x, y, z, player);
                        placeDomeBlock(x, -y, z, player);
                        placeDomeBlock(-x, -y, z, player);
                        placeDomeBlock(x, y, -z, player);
                        placeDomeBlock(-x, y, -z, player);
                        placeDomeBlock(x, -y, -z, player);
                        placeDomeBlock(-x, -y, -z, player);
                    }
                }
            }
        }
        return true;
    }

    private void placeDomeBlock(int x, int y, int z, PlayerEntity player) {
        BlockPos pos = new BlockPos(x, y, z).add(center);
        World world = player.getWorld();
        if (!world.isClient) {
            if (pos.toCenterPos().distanceTo(center.toCenterPos()) <= domeRadius) {
                DomePart part = new DomePart(entity, x+center.getX(), y+center.getY(), z+center.getZ(), true);
                player.getWorld().spawnEntity(part);
                entity.addPart(part);
            }
        }
    }

    @Override
    public boolean tick(ServerPlayerEntity player) {
        if (entity != null) {
            return super.tick(player) || !entity.isAlive() || entity.isRemoved();
        }
        return super.tick(player);
    }

    @Override
    public boolean end(ServerPlayerEntity player) {
        if (entity != null) {
            for (DomePart part : entity.getParts()) {
                if (!part.isRemoved()) part.remove(Entity.RemovalReason.KILLED);
            }
            if (!entity.isRemoved()) entity.remove(Entity.RemovalReason.KILLED);
        }
        for (int x = -domeRadius; x <= domeRadius; x++) {
            for (int y = -domeRadius; y <= domeRadius; y++) {
                for (int z = -domeRadius; z <= domeRadius; z++) {
                    BlockPos pos = new BlockPos(x, y, z).add(center);
                    if (player.getWorld().getBlockState(pos).isOf(SoulForgeBlocks.DETERMINATION_DOME_BLOCK)) {
                        player.getWorld().addBlockBreakParticles(pos, player.getWorld().getBlockState(pos));
                        player.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
        entity = null;
        player.getWorld().playSoundFromEntity(null, player, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 150f, 1f);
        return super.end(player);
    }

    public int getLV() { return 10; }

    public int getCost() { return 40; }

    public int getCooldown() { return 300; }

    @Override
    public AbilityBase getInstance() {
        return new DeterminationDome();
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        if (nbt.contains("center")) nbt.put("center", NbtHelper.fromBlockPos(center));
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        super.readNbt(nbt);
        if (center != null) center = NbtHelper.toBlockPos(nbt.getCompound(("center")));
    }
}
