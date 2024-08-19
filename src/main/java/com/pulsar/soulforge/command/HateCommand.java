package com.pulsar.soulforge.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.ValueComponent;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.text.DecimalFormat;

import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.FloatArgumentType.getFloat;
import static net.minecraft.command.argument.EntityArgumentType.entity;
import static net.minecraft.command.argument.EntityArgumentType.getEntity;

public class HateCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("hate")
                .requires(source -> source.hasPermissionLevel(4))
                .then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>argument("entity", entity())
                        .then(RequiredArgumentBuilder.<ServerCommandSource, Float>argument("amount", floatArg(0f, 100f))
                                .executes(context -> {
                                    if (!Utils.canAccessInverteds(context)) {
                                        context.getSource().sendMessage(Text.literal("Prohibited.").setStyle(Style.EMPTY.withFont(new Identifier("alt"))));
                                        return 1;
                                    }
                                    Entity target = getEntity(context, "entity");
                                    if (target instanceof LivingEntity living) {
                                        ValueComponent values = SoulForge.getValues(living);
                                        values.setFloat("HATE", getFloat(context, "amount"));
                                        DecimalFormat df = new DecimalFormat();
                                        df.setMaximumFractionDigits(2);
                                        String hatePercent = df.format(values.getFloat("HATE")/100f);
                                        context.getSource().sendMessage(Text.literal("Set " + target.getName() + "'s HATE to " + hatePercent));
                                    }
                                    return 1;
                                })
                        )
                )
        );
    }
}
