package com.pulsar.soulforge.networking;

import com.google.common.collect.Sets;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.LinkedList;
import java.util.Set;

public class VeinminePacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        server.execute(() -> {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            playerSoul.resetLastCastTime();
            veinMine(player, pos, player.getWorld().getBlockState(pos));
        });
    }

    private static void veinMine(ServerPlayerEntity playerEntity, BlockPos pos, BlockState sourceState) {
        ItemStack stack = playerEntity.getMainHandStack();
        ServerWorld world = playerEntity.getServerWorld();
        Block source = sourceState.getBlock();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(playerEntity);
        int maxDistance = 200;
        if (playerSoul.getMagic() < 5f) return;
        int blocks = 1;
        Set<BlockPos> visited = Sets.newHashSet(pos);
        LinkedList<Pair<BlockPos, Integer>> candidates = new LinkedList<>(addValidNeighbors(pos, 1));
        LinkedList<BlockPos> toMine = new LinkedList<>();

        while (!candidates.isEmpty() && blocks < 25) {
            Pair<BlockPos, Integer> candidate = candidates.poll();
            BlockPos blockPos = candidate.getLeft();
            int blockDistance = candidate.getRight();
            if (world.getBlockState(blockPos).isOf(source)) {
                toMine.add(blockPos);
                if (visited.add(blockPos)) {
                    if (blockDistance < maxDistance) {
                        for (Pair<BlockPos, Integer> pair : addValidNeighbors(blockPos, blockDistance + 1)) {
                            if (world.getBlockState(pair.getLeft()).isOf(source)) {
                                candidates.add(pair);
                            }
                        }
                    }
                    blocks++;
                }
            }
            if (stopVeining(stack)) break;
        }
        SimpleInventory drops = new SimpleInventory(100);
        for (BlockPos mine : toMine) {
            if (playerSoul.getMagic() < 5f) break;
            BlockState blockState = world.getBlockState(mine);
            Block block = blockState.getBlock();
            if (!blockState.isAir() && blockState.isOf(source)) {
                BlockEntity blockEntity = world.getBlockEntity(mine);
                SoulForge.LOGGER.info("block entity: {}", blockEntity);
                if (blockEntity instanceof Inventory inventory) {
                    SoulForge.LOGGER.info("scattering: {}", inventory.size());
                    ItemScatterer.spawn(world, mine, inventory);
                }
                block.onBreak(world, mine, blockState, playerEntity);
                world.removeBlock(mine, false);
                block.onBroken(world, mine, blockState);
                SoulForge.LOGGER.info("block: {}, block state: {}, block entity: {}", block, blockState, blockEntity);
                playerSoul.setMagic(playerSoul.getMagic() - 5);
                if (playerEntity.canHarvest(blockState) && playerEntity.canModifyBlocks()) {
                    Block.getDroppedStacks(blockState, world, pos, blockEntity, playerEntity, stack).forEach((toDrop) -> {
                        SoulForge.LOGGER.info("dropping: {}", toDrop);
                        drops.addStack(toDrop);
                    });
                    blockState.onStacksDropped(world, pos, stack, true);
                }
            }
        }
        for (ItemStack drop : drops.stacks) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), drop);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }
    }

    private static boolean stopVeining(ItemStack stack) {
        return stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage() - 2;
    }

    private static final Direction[] CARDINAL_DIRECTIONS = new Direction[] {Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH};

    private static LinkedList<Pair<BlockPos, Integer>> addValidNeighbors(BlockPos source, int distance) {
        LinkedList<Pair<BlockPos, Integer>> candidates = new LinkedList<>();
        for (Direction direction : CARDINAL_DIRECTIONS) {
            candidates.add(new Pair<>(source.offset(direction), distance));
        }
        return candidates;
    }
}
