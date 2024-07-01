package com.pulsar.soulforge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.components.WorldComponent;
import com.pulsar.soulforge.components.WorldInitializer;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SoulForgeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("overwrite")
                        .requires(source -> source.hasPermissionLevel(4) || !source.isExecutedByPlayer())
                        .then(literal("player")
                                .then(argument("playerName", player())
                                        .then(literal("get")
                                                .then(literal("trait1")
                                                        .executes(context -> {
                                                            SoulComponent data = SoulForge.getPlayerSoul(getPlayer(context, "playerName"));
                                                            List<TraitBase> traits = data.getTraits();
                                                            String str = "Trait: " + (traits.size() == 2 ? traits.get(0).getName() + "-" + traits.get(1).getName() : traits.get(0).getName());
                                                            context.getSource().sendMessage(Text.literal("Your trait is: " + str));
                                                            return 1;
                                                        })
                                                )
                                                .then(literal("lv")
                                                        .executes(context -> {
                                                            SoulComponent data = SoulForge.getPlayerSoul(getPlayer(context, "playerName"));
                                                            context.getSource().sendMessage(Text.literal("Your LV is: " + data.getLV()));
                                                            return 1;
                                                        })
                                                )
                                                .then(literal("elv")
                                                        .executes(context -> {
                                                            SoulComponent data = SoulForge.getPlayerSoul(getPlayer(context, "playerName"));
                                                            context.getSource().sendMessage(Text.literal("Your effective LV is: " + data.getEffectiveLV()));
                                                            return 1;
                                                        })
                                                )
                                                .then(literal("exp")
                                                        .executes(context -> {
                                                            SoulComponent data = SoulForge.getPlayerSoul(getPlayer(context, "playerName"));
                                                            context.getSource().sendMessage(Text.literal("Your EXP is: " + data.getEXP()));
                                                            return 1;
                                                        })
                                                )
                                        ).then(literal("set")
                                                .then(literal("trait1")
                                                        .then(argument("trait", string())
                                                                .executes(context -> {
                                                                    TraitBase trait = Traits.get(getString(context, "trait"));
                                                                    if (trait != null) {
                                                                        SoulComponent data = SoulForge.getPlayerSoul(getPlayer(context, "playerName"));
                                                                        data.setTrait(0, trait);
                                                                        context.getSource().sendMessage(Text.literal("Your trait has been changed to: " + data.toString()));
                                                                    } else {
                                                                        context.getSource().sendMessage(Text.literal("No trait of name " + getString(context, "trait") + " exists!"));
                                                                    }
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                                .then(literal("trait2")
                                                        .then(argument("trait", string())
                                                                .executes(context -> {
                                                                    SoulComponent data = SoulForge.getPlayerSoul(getPlayer(context, "playerName"));
                                                                    if (getString(context, "trait").equals("None")) {
                                                                        data.setTrait(1, null);
                                                                        context.getSource().sendMessage(Text.literal("Removed your second trait."));
                                                                        return 1;
                                                                    }
                                                                    TraitBase trait = Traits.get(getString(context, "trait"));
                                                                    if (trait != null) {
                                                                        if (data.getTrait(0) == trait) {
                                                                            context.getSource().sendMessage(Text.literal("You cannot have two of the same trait!"));
                                                                            return 1;
                                                                        }
                                                                        data.setTrait(1, trait);
                                                                        context.getSource().sendMessage(Text.literal("Your trait has been changed to: " + data.toString()));
                                                                    } else {
                                                                        context.getSource().sendMessage(Text.literal("No trait of name " + getString(context, "trait") + " exists!"));
                                                                    }
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                                .then(literal("lv")
                                                        .then(argument("amount", integer())
                                                                .executes(context -> {
                                                                    SoulComponent data = SoulForge.getPlayerSoul(getPlayer(context, "playerName"));
                                                                    data.setLV(getInteger(context, "amount"));
                                                                    context.getSource().sendMessage(Text.literal("Set LV to " + data.getLV()));
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                                .then(literal("exp")
                                                        .then(argument("amount", integer())
                                                                .executes(context -> {
                                                                    SoulComponent data = SoulForge.getPlayerSoul(getPlayer(context, "playerName"));
                                                                    data.setEXP(getInteger(context, "amount"));
                                                                    context.getSource().sendMessage(Text.literal("Set EXP to " + data.getEXP()));
                                                                    return 1;
                                                                })
                                                        )
                                                ).then(literal("power")
                                                        .then(literal("normal")
                                                                .executes(context -> {
                                                                    SoulComponent data = SoulForge.getPlayerSoul(getPlayer(context, "playerName"));
                                                                    data.setStrong(false);
                                                                    data.setPure(false);
                                                                    context.getSource().sendMessage(Text.literal("Set your power to NORMAL."));
                                                                    return 1;
                                                                })
                                                        )
                                                        .then(literal("strong")
                                                                .executes(context -> {
                                                                    SoulComponent data = SoulForge.getPlayerSoul(getPlayer(context, "playerName"));
                                                                    data.setStrong(true);
                                                                    data.setPure(false);
                                                                    context.getSource().sendMessage(Text.literal("Set your power to STRONG."));
                                                                    return 1;
                                                                })
                                                        )
                                                        .then(literal("pure")
                                                                .executes(context -> {
                                                                    SoulComponent data = SoulForge.getPlayerSoul(getPlayer(context, "playerName"));
                                                                    data.setStrong(true);
                                                                    data.setPure(true);
                                                                    context.getSource().sendMessage(Text.literal("Set your power to PURE."));
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        ).then(literal("reset")
                                                .executes(context -> {
                                                    SoulComponent data = SoulForge.getPlayerSoul(getPlayer(context, "playerName"));
                                                    data.reset();
                                                    context.getSource().sendMessage(Text.literal("You have been reset!"));
                                                    return 1;
                                                })
                                                .then(literal("discoveredAbilities")
                                                        .executes(context -> {
                                                            SoulComponent data = SoulForge.getPlayerSoul(getPlayer(context, "playerName"));
                                                            data.clearDiscovered();
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )
                        .then(literal("world")
                                .then(literal("config")
                                        .then(literal("expMultiplier")
                                                .executes(context -> {
                                                    WorldComponent data = SoulForge.getWorldComponent(context.getSource().getWorld());
                                                    context.getSource().sendMessage(Text.literal(String.valueOf(data.getExpMultiplier())));
                                                    return 1;
                                                })
                                                .then(argument("multiplier", floatArg(0f, 1024f))
                                                        .executes(context -> {
                                                            WorldComponent data = SoulForge.getWorldComponent(context.getSource().getWorld());
                                                            data.setExpMultiplier(getFloat(context, "multiplier"));
                                                            context.getSource().sendMessage(Text.literal("Value modified."));
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )
        );
    }
}
