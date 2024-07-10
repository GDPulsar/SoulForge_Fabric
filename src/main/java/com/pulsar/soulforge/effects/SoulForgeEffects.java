package com.pulsar.soulforge.effects;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SoulForgeEffects {
    public static final StatusEffect VULNERABILITY = new VulnerabilityEffect();
    public static final StatusEffect MANA_SICKNESS = new ManaSickness();
    public static final StatusEffect MANA_OVERLOAD = new ManaOverload();
    public static final StatusEffect SNOWED_VISION = new SnowedVision();
    public static final StatusEffect VALIANT_HEART = new ValiantHeart();
    public static final StatusEffect CRUSHED = new CrushedEffect();
    public static final StatusEffect FROSTBITE = new FrostbiteEffect();
    public static final StatusEffect FROSTBURN = new FrostburnEffect();
    public static final StatusEffect CREATIVE_ZONE = new CreativeZoneEffect();
    //public static final StatusEffect UNCHAINED_EFFECT = new UnchainedEffect();

    public static void registerEffects() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SoulForge.MOD_ID, "vulnerability"), VULNERABILITY);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SoulForge.MOD_ID, "mana_sickness"), MANA_SICKNESS);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SoulForge.MOD_ID, "mana_overload"), MANA_OVERLOAD);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SoulForge.MOD_ID, "snowed_vision"), SNOWED_VISION);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SoulForge.MOD_ID, "valiant_heart"), VALIANT_HEART);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SoulForge.MOD_ID, "crushed"), CRUSHED);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SoulForge.MOD_ID, "frostbite"), FROSTBITE);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SoulForge.MOD_ID, "frostburn"), FROSTBURN);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SoulForge.MOD_ID, "creative_zone"), CREATIVE_ZONE);
        //Registry.register(Registries.STATUS_EFFECT, new Identifier(SoulForge.MOD_ID, "unchained_soul"), UNCHAINED_EFFECT);
    }
}
