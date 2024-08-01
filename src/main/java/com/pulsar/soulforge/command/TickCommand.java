package com.pulsar.soulforge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pulsar.soulforge.accessors.HasTickManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class TickCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("tick")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("freeze")
                                .executes(context -> {
                                    MinecraftServer server = context.getSource().getServer();
                                    ((HasTickManager)server).getTickManager().setFrozen(true);
                                    return 1;
                                })
                        ).then(literal("unfreeze")
                                .executes(context -> {
                                    MinecraftServer server = context.getSource().getServer();
                                    ((HasTickManager)server).getTickManager().setFrozen(false);
                                    return 1;
                                })
                        )
        );
    }
}
