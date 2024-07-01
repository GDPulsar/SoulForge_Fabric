package com.pulsar.soulforge.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TraitArgumentType implements ArgumentType<TraitBase> {
    public static final DynamicCommandExceptionType TRAIT_NOT_FOUND = new DynamicCommandExceptionType(
            o -> Text.translatable("commands.soulforge.trait_not_found", o)
    );

    public static TraitArgumentType trait() {
        return new TraitArgumentType();
    }

    @Override
    public TraitBase parse(StringReader stringReader) throws CommandSyntaxException {
        int argBeginning = stringReader.getCursor();
        if (!stringReader.canRead()) stringReader.skip();

        while (stringReader.canRead() && stringReader.peek() != ' ') stringReader.skip();
        String traitStr = stringReader.getString().substring(argBeginning, stringReader.getCursor());
        try {
            return Traits.get(traitStr);
        } catch (IllegalArgumentException e) {
            throw TRAIT_NOT_FOUND.create(traitStr);
        }
    }

    public static TraitBase getTrait(CommandContext<ServerCommandSource> context, String argumentName) throws CommandSyntaxException {
        String str = context.getArgument(argumentName, String.class);

        try {
            return Traits.get(str);
        } catch (IllegalArgumentException e) {
            throw TRAIT_NOT_FOUND.create(str);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> availableTraits = new ArrayList<>();

        for (TraitBase trait : Traits.all()) {
            availableTraits.add(trait.getName());
        }

        return CommandSource.suggestMatching(availableTraits, builder);
    }
}
