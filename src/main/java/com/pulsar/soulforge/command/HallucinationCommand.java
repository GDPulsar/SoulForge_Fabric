package com.pulsar.soulforge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HallucinationCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(
                literal("ghost")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(argument("player", player())
                                .then(argument("distance", integer())
                                        .executes(context -> {
                                            ServerPlayerEntity player = getPlayer(context, "player");
                                            int distance = getInteger(context, "distance");
                                            float angle = (float)(Math.random()*Math.PI*2f);
                                            int xPos = MathHelper.floor(Math.sin(angle)*distance);
                                            int zPos = MathHelper.floor(Math.cos(angle)*distance);
                                            try {
                                                BlockPos spawnPos = Utils.getTopBlock(player.getServer(), player.getWorld(), xPos, zPos);

                                            } catch (Exception ignored) {}
                                            return 1;
                                        })
                                )
                        )
        );
    }
}
