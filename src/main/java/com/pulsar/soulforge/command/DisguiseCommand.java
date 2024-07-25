package com.pulsar.soulforge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DisguiseCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("disguise")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(argument("player", player())
                                .then(argument("target", player())
                                        .executes(context -> {
                                                SoulComponent playerSoul = SoulForge.getPlayerSoul(getPlayer(context, "player"));
                                                PlayerEntity target = getPlayer(context, "target");
                                                playerSoul.setDisguise(target);
                                                context.getSource().sendMessage(Text.literal("Disguised as ").append(target.getDisplayName()).append("."));
                                                return 1;
                                        })
                                )
                                .executes(context -> {
                                    SoulComponent playerSoul = SoulForge.getPlayerSoul(getPlayer(context, "player"));
                                    playerSoul.removeDisguise();
                                    context.getSource().sendMessage(Text.literal("Removed your disguise."));
                                    return 1;
                                })
                        )
        );
    }
}
