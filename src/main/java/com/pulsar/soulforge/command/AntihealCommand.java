package com.pulsar.soulforge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.WorldComponent;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.text.DecimalFormat;

import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class AntihealCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("antiheal")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(argument("player", player())
                                .then(argument("amount", floatArg())
                                        .then(argument("duration", integer())
                                                .executes(context -> {
                                                    SoulComponent playerSoul = SoulForge.getPlayerSoul(getPlayer(context, "player"));
                                                    float antiheal = getFloat(context, "amount")/100f;
                                                    int duration = getInteger(context, "duration");
                                                    DecimalFormat df = new DecimalFormat();
                                                    df.setMaximumFractionDigits(2);
                                                    String antihealPercent = df.format(antiheal*100f);
                                                    playerSoul.setValue("antiheal", antiheal);
                                                    playerSoul.setValue("antihealDuration", duration);
                                                    context.getSource().sendMessage(Text.literal("Set " + antihealPercent + "% for " + duration + " ticks."));
                                                    return 1;
                                                })
                                        )
                                )
                        )
        );
    }
}
