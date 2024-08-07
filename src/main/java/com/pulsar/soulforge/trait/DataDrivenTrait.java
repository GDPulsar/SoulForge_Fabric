package com.pulsar.soulforge.trait;

import com.pulsar.soulforge.ability.AbilityBase;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public class DataDrivenTrait implements TraitBase {
    public String name;
    public String translationKey;
    public AbilityBase[] abilities;
    public Optional<Style> traitStyle;
    public int color;

    public DataDrivenTrait(String name, String translationKey, AbilityBase[] abilities, Optional<Style> traitStyle, int color) {
        this.name = name;
        this.translationKey = translationKey;
        this.abilities = abilities;
        this.traitStyle = traitStyle;
        this.color = color;
    }

    public String name() { return this.name; }
    public String translationKey() { return this.translationKey; }
    public AbilityBase[] abilities() { return this.abilities; }
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
        return List.of(abilities);
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
