package com.pulsar.soulforge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.pulsar.soulforge.ai.HATEBaseAI;
import com.pulsar.soulforge.ai.NetworkSerializer;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.MathHelper;

import java.io.File;
import java.nio.file.Path;
import java.text.DecimalFormat;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.command.argument.EntityArgumentType.entity;
import static net.minecraft.command.argument.EntityArgumentType.getEntity;

public class HateCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("hate")
                .requires(source -> source.hasPermissionLevel(4) && Utils.canAccessInverteds(source))
                .then(LiteralArgumentBuilder.<ServerCommandSource>literal("set")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>argument("entity", entity())
                                .then(RequiredArgumentBuilder.<ServerCommandSource, Float>argument("amount", floatArg(0f, 100f))
                                        .executes(context -> {
                                            if (!Utils.canAccessInverteds(context)) {
                                                context.getSource().sendMessage(Text.literal("Prohibited.").setStyle(Style.EMPTY.withFont(new Identifier("alt"))));
                                                return 1;
                                            }
                                            Entity target = getEntity(context, "entity");
                                            if (target instanceof LivingEntity living) {
                                                Utils.setHate(living, getFloat(context, "amount"));
                                                DecimalFormat df = new DecimalFormat();
                                                df.setMaximumFractionDigits(2);
                                                String hatePercent = df.format(Utils.getHate(living));
                                                context.getSource().sendMessage(Text.literal("Set ").append(target.getName()).append("'s HATE to " + hatePercent));
                                            }
                                            return 1;
                                        })
                                ).then(LiteralArgumentBuilder.<ServerCommandSource>literal("hasHate")
                                        .then(RequiredArgumentBuilder.argument("hasHate", bool()))
                                                .executes(context -> {
                                                    if (!Utils.canAccessInverteds(context)) {
                                                        context.getSource().sendMessage(Text.literal("Prohibited.").setStyle(Style.EMPTY.withFont(new Identifier("alt"))));
                                                        return 1;
                                                    }
                                                    Entity target = getEntity(context, "entity");
                                                    if (target instanceof LivingEntity living) {
                                                        boolean hasHate = getBool(context, "hasHate");
                                                        Utils.setHasHate(living, hasHate);
                                                        if (!hasHate) {
                                                            context.getSource().sendMessage(Text.literal("Removed ").append(target.getName()).append("'s HATE"));
                                                        } else {
                                                            context.getSource().sendMessage(Text.literal("Gave ").append(target.getName()).append(" HATE"));
                                                        }
                                                    }
                                                    return 1;
                                                })
                                )
                        )
                ).then(LiteralArgumentBuilder.<ServerCommandSource>literal("ai")
                        .requires(ServerCommandSource::isExecutedByPlayer)
                        .then(LiteralArgumentBuilder.<ServerCommandSource>literal("predict")
                                .executes(context -> {
                                    if (!Utils.canAccessInverteds(context)) {
                                        context.getSource().sendMessage(Text.literal("Prohibited.").setStyle(Style.EMPTY.withFont(new Identifier("alt"))));
                                        return 1;
                                    }
                                    PlayerEntity player = context.getSource().getPlayer();
                                    double[] outputs = HATEBaseAI.getOutputs(player);
                                    HATEBaseAI.AiResult result = HATEBaseAI.getResult(outputs);
                                    context.getSource().sendMessage(Text.literal("AI result is " + result));
                                    context.getSource().sendMessage(Text.literal("Results were:"));
                                    for (int i = 0 ; i < outputs.length; i++) {
                                        context.getSource().sendMessage(Text.literal("Output " + i + ": " + outputs[i]));
                                    }
                                    return 1;
                                })
                        ).then(LiteralArgumentBuilder.<ServerCommandSource>literal("train")
                                .then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("values", greedyString())
                                        .executes(context -> {
                                            PlayerEntity player = context.getSource().getPlayer();
                                            String[] strValues = getString(context, "values").split(" ");
                                            double[] expected = new double[strValues.length];
                                            for (int i = 0; i < strValues.length; i++) {
                                                expected[i] = MathHelper.clamp(Double.parseDouble(strValues[i]), 0, 1);
                                            }
                                            HATEBaseAI.train(player, expected);
                                            return 1;
                                        })
                                ).then(LiteralArgumentBuilder.<ServerCommandSource>literal("example")
                                        .then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("values", greedyString())
                                                .executes(context -> {
                                                    PlayerEntity player = context.getSource().getPlayer();
                                                    String[] strValues = getString(context, "values").split(" ");
                                                    double[] expected = new double[strValues.length];
                                                    for (int i = 0; i < strValues.length; i++) {
                                                        expected[i] = MathHelper.clamp(Double.parseDouble(strValues[i]), 0, 1);
                                                    }
                                                    double[] inputs = HATEBaseAI.getInputsFromEntity(player);
                                                    Path path = context.getSource().getServer().getSavePath(WorldSavePath.ROOT);
                                                    String strPath = path.toString() + "\\networks";
                                                    File directory = new File(strPath);
                                                    if (!directory.exists()) directory.mkdir();
                                                    strPath += "\\training_data.json";
                                                    NetworkSerializer.writeTrainingData(strPath, inputs, expected);
                                                    return 1;
                                                })
                                        )
                                )
                        ).then(LiteralArgumentBuilder.<ServerCommandSource>literal("inputs")
                                .executes(context -> {
                                    PlayerEntity player = context.getSource().getPlayer();
                                    if (player != null) {
                                        double[] inputs = HATEBaseAI.getInputsFromEntity(player);
                                        context.getSource().sendMessage(Text.literal("AI inputs:"));
                                        for (int i = 0; i < inputs.length; i++) {
                                            context.getSource().sendMessage(Text.literal("Input " + i + ": " + inputs[i]));
                                        }
                                    }
                                    return 1;
                                })
                        ).then(LiteralArgumentBuilder.<ServerCommandSource>literal("save")
                                .executes(context -> {
                                    Path path = context.getSource().getServer().getSavePath(WorldSavePath.ROOT);
                                    String strPath = path.toString() + "\\networks";
                                    File directory = new File(strPath);
                                    if (!directory.exists()) directory.mkdir();
                                    strPath += "\\hate_base.souljafrog";
                                    NetworkSerializer.writeNetworkToFile(HATEBaseAI.network, strPath);
                                    context.getSource().sendMessage(Text.literal("Network saved!"));
                                    return 1;
                                })
                        ).then(LiteralArgumentBuilder.<ServerCommandSource>literal("load")
                                .executes(context -> {
                                    Path path = context.getSource().getServer().getSavePath(WorldSavePath.ROOT);
                                    String strPath = path.toString() + "\\networks";
                                    File directory = new File(strPath);
                                    if (!directory.exists()) directory.mkdir();
                                    strPath += "\\hate_base.souljafrog";
                                    File file = new File(strPath);
                                    if (file.exists()) {
                                        HATEBaseAI.network = NetworkSerializer.readNetworkFromFile(strPath);
                                        context.getSource().sendMessage(Text.literal("Network loaded!"));
                                    } else {
                                        context.getSource().sendMessage(Text.literal("Network file does not exist."));
                                    }
                                    return 1;
                                })
                        )
                )
        );
    }
}
