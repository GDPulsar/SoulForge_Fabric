package com.pulsar.soulforge.trait.traits;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.patience.*;
import com.pulsar.soulforge.trait.TraitBase;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Patience implements TraitBase {
    public final String name = "Patience";
    public final Identifier identifier = new Identifier(SoulForge.MOD_ID, "patience");
    public final List<AbilityBase> abilities = new ArrayList<>(Arrays.asList(
            new BlindingSnowstorm(),
            new FreezeRing(),
            new Snowglobe(),
            new FrostWave(),
            new SkewerWeakpoint(),
            new FrozenGrasp(),
            new Iceshock(),
            new WeatherWarning(),
            new Snowgrave()
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
        return 0x00FFFF;
    }
}
