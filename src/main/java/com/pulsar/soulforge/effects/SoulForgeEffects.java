package com.pulsar.soulforge.effects;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class SoulForgeEffects {
    public static RegistryEntry<StatusEffect> VULNERABILITY;
    public static RegistryEntry<StatusEffect> MANA_OVERLOAD;
    public static RegistryEntry<StatusEffect> SNOWED_VISION;
    public static RegistryEntry<StatusEffect> VALIANT_HEART;
    public static RegistryEntry<StatusEffect> CRUSHED;
    public static RegistryEntry<StatusEffect> FROSTBITE;
    public static RegistryEntry<StatusEffect> FROSTBURN;
    public static RegistryEntry<StatusEffect> CREATIVE_ZONE;

    public static void registerEffects() {
        VULNERABILITY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SoulForge.MOD_ID, "vulnerability"), new VulnerabilityEffect());
        MANA_OVERLOAD = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SoulForge.MOD_ID, "mana_overload"), new ManaOverload());
        SNOWED_VISION = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SoulForge.MOD_ID, "snowed_vision"), new SnowedVision());
        VALIANT_HEART = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SoulForge.MOD_ID, "valiant_heart"), new ValiantHeart());
        CRUSHED = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SoulForge.MOD_ID, "crushed"), new CrushedEffect());
        FROSTBITE = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SoulForge.MOD_ID, "frostbite"), new FrostbiteEffect());
        FROSTBURN = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SoulForge.MOD_ID, "frostburn"), new FrostburnEffect());
        CREATIVE_ZONE = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SoulForge.MOD_ID, "creative_zone"), new CreativeZoneEffect());
    }
}
