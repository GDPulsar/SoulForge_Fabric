package com.pulsar.soulforge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Set;

import static net.minecraft.server.command.CommandManager.literal;

public class WormholeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(
                literal("acceptwormhole")
                        .requires(ServerCommandSource::isExecutedByPlayer)
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
                            if (playerSoul.hasWormholeRequest()) {
                                PlayerEntity fromPlayer = playerSoul.getWormholeTarget();
                                fromPlayer.teleport(context.getSource().getWorld(), player.getX(), player.getY(), player.getZ(), Set.of(), player.getYaw(), player.getPitch());
                                playerSoul.removeWormholeRequest();
                            }
                            return 1;
                        })
        );
    }
}
