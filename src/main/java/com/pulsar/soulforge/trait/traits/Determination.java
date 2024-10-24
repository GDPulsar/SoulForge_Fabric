package com.pulsar.soulforge.trait.traits;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.determination.*;
import com.pulsar.soulforge.trait.TraitBase;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Determination implements TraitBase {
    public final String name = "Determination";
    public final Identifier identifier = new Identifier(SoulForge.MOD_ID, "determination");
    public final List<AbilityBase> abilities = new ArrayList<AbilityBase>(Arrays.asList(
            new DeBuff(),
            new DeterminationAura(),
            new DeterminationBlaster(),
            new DeterminationDome(),
            new DeterminationSword(),
            new DeterminationShot(),
            new DeterminationPlatform(),
            new LimitBreak(),
            new Regeneration(),
            new SAVELOAD(),
            new TrueLOVE(),
            new UnchainedSoul(),
            new WeaponWheel()
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
        return 0xFF0000;
    }
}
