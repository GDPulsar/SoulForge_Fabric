package com.pulsar.soulforge.trait.traits;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.justice.*;
import com.pulsar.soulforge.trait.TraitBase;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Justice implements TraitBase {
    public final String name = "Justice";
    public final Identifier identifier = new Identifier(SoulForge.MOD_ID, "justice");
    public final List<AbilityBase> abilities = new ArrayList<>(Arrays.asList(
            new AutoTurret(),
            new BulletRing(),
            new Railcannon(),
            new JusticeBow(),
            new JusticeCrossbow(),
            new JusticeRevolver(),
            new JusticePellets(),
            new Launch(),
            new FragmentationGrenade(),
            new OrbitalStrike(),
            new ShotgunFist()
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
    public Formatting getFormatting() { return Formatting.YELLOW; }

    @Override
    public int getColor() {
        return 0xFFFF00;
    }
}
