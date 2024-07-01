package com.pulsar.soulforge.networking;

import com.google.common.collect.Sets;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.block.SoulForgeBlockEntity;
import com.pulsar.soulforge.components.SoulComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.Set;

public record VeinminePacket(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<VeinminePacket> ID = new Id<>(SoulForgeNetworking.VEINMINE);
    public static final PacketCodec<RegistryByteBuf, VeinminePacket> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, VeinminePacket::pos,
            VeinminePacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(VeinminePacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        BlockPos pos = packet.pos();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.resetLastCastTime();
        veinMine(player, pos, player.getWorld().getBlockState(pos));
    }

    private static void veinMine(ServerPlayerEntity playerEntity, BlockPos pos, BlockState sourceState) {
        ItemStack stack = playerEntity.getMainHandStack();
        ServerWorld world = playerEntity.getServerWorld();
        Block source = sourceState.getBlock();
        SoulComponent playerSoul = SoulForge.getPlayerSoul(playerEntity);
        int maxBlocks = (int)playerSoul.getMagic()/5;
        int maxDistance = 200;

        if (maxBlocks <= 0) return;
        int blocks = 1;
        Set<BlockPos> visited = Sets.newHashSet(pos);
        LinkedList<Pair<BlockPos, Integer>> candidates = new LinkedList<>(addValidNeighbors(pos, 1));
        LinkedList<BlockPos> toMine = new LinkedList<>();

        while (!candidates.isEmpty() && blocks < maxBlocks) {
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
            BlockState blockState = world.getBlockState(mine);
            Block block = blockState.getBlock();
            if (!blockState.isAir() && blockState.isOf(source)) {
                block.onBreak(world, mine, blockState, playerEntity);
                FluidState fluidState = world.getFluidState(mine);
                if (world.getBlockEntity(mine) != null) {
                    Inventory inventory = (Inventory)world.getBlockEntity(mine);
                    if (inventory != null) {
                        for (int i = 0; i < inventory.size(); i++) {
                            drops.addStack(inventory.getStack(i));
                        }
                    }
                }
                boolean bl = world.setBlockState(mine, fluidState.getBlockState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                if (bl) {
                    block.onBroken(world, mine, blockState);
                }
                playerSoul.setMagic(playerSoul.getMagic() - 5);
                if (playerEntity.canHarvest(blockState) && playerEntity.canModifyBlocks()) {
                    LootContextParameterSet.Builder builder = (new LootContextParameterSet.Builder(world)).add(LootContextParameters.ORIGIN, Vec3d.ofCenter(mine)).add(LootContextParameters.TOOL, stack).addOptional(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(mine));
                    for (ItemStack droppedStack : blockState.getDroppedStacks(builder)) {
                        drops.addStack(droppedStack);
                    }
                }
            }
        }
        for (ItemStack drop : drops.heldStacks) {
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
