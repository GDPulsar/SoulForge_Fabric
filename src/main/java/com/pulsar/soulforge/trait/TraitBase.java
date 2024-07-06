package com.pulsar.soulforge.trait;

import com.pulsar.soulforge.ability.AbilityBase;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.List;

public interface TraitBase {
    String getName();
    MutableText getLocalizedText();
    List<AbilityBase> getAbilities();
    Formatting getFormatting();
    int getColor();
}
