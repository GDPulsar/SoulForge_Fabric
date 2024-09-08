package com.pulsar.soulforge.trait;

import com.pulsar.soulforge.ability.Abilities;
import com.pulsar.soulforge.ability.AbilityBase;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class DataDrivenTrait implements TraitBase {
    public String name;
    public Identifier id;
    public List<Identifier> abilities;
    public Identifier pureAbility;
    public Style traitStyle;
    public int color;

    public DataDrivenTrait(Identifier id, String name, List<Identifier> abilities, Identifier pureAbility, Style traitStyle, int color) {
        this.id = id;
        this.name = name;
        this.abilities = abilities;
        this.pureAbility = pureAbility;
        this.traitStyle = traitStyle;
        this.color = color;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MutableText getLocalizedText() {
        return Text.translatable("trait." + id.getPath() + ".name").setStyle(getStyle());
    }

    @Override
    public List<AbilityBase> getAbilities() {
        return abilities.stream().map(Abilities::get).toList();
    }

    @Override
    public Style getStyle() {
        return Optional.ofNullable(traitStyle).orElse(Style.EMPTY.withColor(getColor()));
    }

    @Override
    public int getColor() {
        return color;
    }
}
