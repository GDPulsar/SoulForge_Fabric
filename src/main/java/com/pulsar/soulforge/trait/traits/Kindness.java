package com.pulsar.soulforge.trait.traits;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.kindness.*;
import com.pulsar.soulforge.trait.TraitBase;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Kindness implements TraitBase {
    public final String name = "Kindness";
    public final Identifier identifier = Identifier.of(SoulForge.MOD_ID, "kindness");
    public final List<AbilityBase> abilities = new ArrayList<>(Arrays.asList(
            new AllyHeal(),
            new Overclock(),
            new ExpandingForce(),
            new Immobilization(),
            new KindnessDome(),
            new KindnessShield(),
            new PainSplit(),
            new ProtectiveTouch(),
            new SelfHeal()
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
    public int getColor() {
        return 0x00FF00;
    }
}
