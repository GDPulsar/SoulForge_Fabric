package com.pulsar.soulforge.trait.traits;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.perseverance.*;
import com.pulsar.soulforge.trait.TraitBase;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Perseverance implements TraitBase {
    public final String name = "Perseverance";
    public final Identifier identifier = new Identifier(SoulForge.MOD_ID, "perseverance");
    public final List<AbilityBase> abilities = new ArrayList<>(Arrays.asList(
            new Furioso(),
            new ColossalClaymore(),
            new MorphingWeaponry(),
            new Onrush(),
            new PerseveranceAura(),
            new RendAsunder()
    ));

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MutableText getLocalizedText() { return Text.translatable("trait."+identifier.getPath()+".name"); }

    @Override
    public List<AbilityBase> getAbilities() {
        return abilities;
    }

    @Override
    public Formatting getFormatting() { return Formatting.DARK_PURPLE; }

    @Override
    public int getColor() {
        return 0x8000FF;
    }
}
