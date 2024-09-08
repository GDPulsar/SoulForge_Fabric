package com.pulsar.soulforge.trait.traits;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.integrity.*;
import com.pulsar.soulforge.trait.TraitBase;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Integrity implements TraitBase {
    public final String name = "Integrity";
    public final Identifier identifier = new Identifier(SoulForge.MOD_ID, "integrity");
    public final List<AbilityBase> abilities = new ArrayList<>(Arrays.asList(
            new AntigravityZone(),
            new GravityAnchor(),
            new IntegrityRapier(),
            new KineticBoost(),
            new Platforms(),
            new RepulsionField(),
            new Telekinesis(),
            new TelekineticShockwave(),
            new Warpspeed()
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
    public Style getStyle() { return Style.EMPTY.withColor(getColor()); }

    @Override
    public int getColor() {
        return 0x0000FF;
    }
}
