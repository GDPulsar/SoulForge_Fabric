package com.pulsar.soulforge.trait.traits;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.bravery.*;
import com.pulsar.soulforge.ability.determination.*;
import com.pulsar.soulforge.ability.integrity.*;
import com.pulsar.soulforge.ability.justice.*;
import com.pulsar.soulforge.ability.kindness.*;
import com.pulsar.soulforge.ability.patience.*;
import com.pulsar.soulforge.ability.perseverance.*;
import com.pulsar.soulforge.trait.TraitBase;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Spite implements TraitBase {
    public final String name = "Spite";
    public final Identifier identifier = new Identifier(SoulForge.MOD_ID, "spite");
    public final List<AbilityBase> abilities = new ArrayList<>(Arrays.asList(
            new BraveryBoost(),
            new BraveryGauntlets(),
            new BraveryHammer(),
            new BraverySpear(),
            new EnergyBall(),
            new EnergyWave(),
            new Eruption(),
            new Flamethrower(),
            new Shatter(),
            new Polarities(),
            new ValiantHeart(),
            new DeBuff(),
            new DeterminationAura(),
            new DeterminationBlaster(),
            new DeterminationDome(),
            new DeterminationSword(),
            new DeterminationPlatform(),
            new DeterminationShot(),
            new LimitBreak(),
            new Regeneration(),
            new SAVELOAD(),
            new TrueLOVE(),
            new UnchainedSoul(),
            new WeaponWheel(),
            new AntigravityZone(),
            new GravityAnchor(),
            new IntegrityRapier(),
            new KineticBoost(),
            new Platforms(),
            new RepulsionField(),
            new TelekinesisEntity(),
            new TelekineticShockwave(),
            new Warpspeed(),
            new AutoTurret(),
            new BulletRing(),
            new FragmentationGrenade(),
            new JusticeBow(),
            new JusticeCrossbow(),
            new JusticePellets(),
            new JusticeRevolver(),
            new Launch(),
            new Railcannon(),
            new OrbitalStrike(),
            new ShotgunFist(),
            new AllyHeal(),
            new Overclock(),
            new ExpandingForce(),
            new Immobilization(),
            new KindnessDome(),
            new KindnessShield(),
            new PainSplit(),
            new ProtectiveTouch(),
            new SelfHeal(),
            new BlindingSnowstorm(),
            new FreezeRing(),
            new Snowglobe(),
            new FrostWave(),
            new SkewerWeakpoint(),
            new IceSpike(),
            new Slowball(),
            new TotalFrostbite(),
            new Proceed(),
            new ColossalClaymore(),
            new MorphingWeaponry(),
            new Furioso(),
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
    public Style getStyle() { return Style.EMPTY.withColor(getColor()); }

    @Override
    public int getColor() {
        return 0xB20000;
    }
}
