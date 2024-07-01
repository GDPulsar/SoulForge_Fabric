package com.pulsar.soulforge.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import static net.minecraft.command.argument.BlockPosArgumentType.blockPos;
import static net.minecraft.command.argument.BlockPosArgumentType.getBlockPos;
import static net.minecraft.command.argument.BlockStateArgumentType.blockState;
import static net.minecraft.command.argument.BlockStateArgumentType.getBlockState;
import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GhostBlockCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(
                literal("ghost")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(argument("player", player())
                                .then(argument("position", blockPos())
                                        .then(argument("block", blockState(commandRegistryAccess))
                                                .executes(context -> {
                                                    BlockPos pos = getBlockPos(context, "position");
                                                    ServerPlayerEntity player = getPlayer(context, "player");
                                                    BlockState state = getBlockState(context, "block").getBlockState();
                                                    player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, state));
                                                    return 1;
                                                })
                                        )
                                        .then(argument("position2", blockPos())
                                                .then(argument("block", blockState(commandRegistryAccess))
                                                        .executes(context -> {
                                                            BlockState state = getBlockState(context, "block").getBlockState();
                                                            ServerPlayerEntity player = getPlayer(context, "player");
                                                            BlockPos pos1 = getBlockPos(context, "position");
                                                            BlockPos pos2 = getBlockPos(context, "position2");
                                                            Box box = Box.enclosing(pos1, pos2);
                                                            for (int x = (int)box.minX; x <= box.maxX; x++) {
                                                                for (int y = (int)box.minY; y <= box.maxY; y++) {
                                                                    for (int z = (int)box.minZ; z <= box.maxZ; z++) {
                                                                        BlockPos pos = new BlockPos(x, y, z);
                                                                        player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, state));
                                                                    }
                                                                }
                                                            }
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )
        );
    }
}
