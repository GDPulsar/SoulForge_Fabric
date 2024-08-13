package com.pulsar.soulforge.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pulsar.soulforge.ability.Abilities;
import com.pulsar.soulforge.ability.AbilityBase;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataDrivenTrait implements TraitBase {
    public String name;
    public String translationKey;
    public List<AbilityBase> abilities;
    public Optional<Style> traitStyle;
    public int color;

    public static Codec<DataDrivenTrait> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(DataDrivenTrait::name),
            Codec.STRING.fieldOf("translationKey").forGetter(DataDrivenTrait::translationKey),
            Codec.list(Identifier.CODEC).fieldOf("abilities").forGetter(DataDrivenTrait::abilities),
            Style.CODEC.optionalFieldOf("traitStyle").forGetter(DataDrivenTrait::style),
            Codec.INT.fieldOf("color").forGetter(DataDrivenTrait::color)
    ).apply(instance, DataDrivenTrait::new));

    public DataDrivenTrait(String name, String translationKey, List<Identifier> abilities, Optional<Style> traitStyle, int color) {
        this.name = name;
        this.translationKey = translationKey;
        this.abilities = new ArrayList<>();
        for (Identifier id : abilities) {
            this.abilities.add(Abilities.get(id));
        }
        this.traitStyle = traitStyle;
        this.color = color;
    }

    public String name() { return this.name; }
    public String translationKey() { return this.translationKey; }
    public List<Identifier> abilities() {
        List<Identifier> ids = new ArrayList<>();
        for (AbilityBase ability : this.abilities) {
            ids.add(ability.getID());
        }
        return ids;
    }
    public Optional<Style> style() { return this.traitStyle; }
    public int color() { return this.color; }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MutableText getLocalizedText() {
        return Text.translatable(translationKey).setStyle(getStyle());
    }

    @Override
    public List<AbilityBase> getAbilities() {
        return abilities;
    }

    @Override
    public Style getStyle() {
        return traitStyle.orElse(Style.EMPTY.withColor(getColor()));
    }

    @Override
    public int getColor() {
        return color;
    }
}
