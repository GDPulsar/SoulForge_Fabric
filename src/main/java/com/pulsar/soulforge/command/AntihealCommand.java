package com.pulsar.soulforge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.attribute.SoulForgeAttributes;
import com.pulsar.soulforge.components.TemporaryModifierComponent;
import com.pulsar.soulforge.util.Triplet;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.text.DecimalFormat;

import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.argument.EntityArgumentType.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class AntihealCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("antiheal")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(argument("target", entity())
                                .then(argument("amount", floatArg())
                                        .then(argument("duration", integer())
                                                .executes(context -> {
                                                    if (getEntity(context, "target") instanceof LivingEntity living) {
                                                        float antiheal = getFloat(context, "amount") / 100f;
                                                        int duration = getInteger(context, "duration");
                                                        DecimalFormat df = new DecimalFormat();
                                                        df.setMaximumFractionDigits(2);
                                                        String antihealPercent = df.format(antiheal * 100f);
                                                        Utils.addAntiheal(antiheal, duration, living);
                                                        context.getSource().sendMessage(Text.literal("Set " + antihealPercent + "% for " + duration + " ticks."));
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                                .executes(context -> {
                                    if (getEntity(context, "target") instanceof LivingEntity living) {
                                        TemporaryModifierComponent modifiers = SoulForge.getTemporaryModifiers(living);
                                        Triplet<EntityAttributeModifier, EntityAttribute, Float> modifier = modifiers.getModifierEntry(SoulForgeAttributes.ANTIHEAL, Utils.antihealModifierID);
                                        if (modifier != null) {
                                            double antiheal = modifier.getFirst().getValue();
                                            int duration = modifier.getThird().intValue();
                                            DecimalFormat df = new DecimalFormat();
                                            df.setMaximumFractionDigits(2);
                                            String antihealPercent = df.format(antiheal * 100f);
                                            context.getSource().sendMessage(Text.literal(getPlayer(context, "player").getDisplayName().getString() + " has " + antihealPercent + "% antiheal for " + duration + " ticks."));
                                        }
                                    }
                                    return 1;
                                })
                        )
        );
    }
}
