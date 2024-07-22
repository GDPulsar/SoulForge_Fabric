package com.pulsar.soulforge.damage_type;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SoulForgeDamageTypes {
    public static RegistryKey<DamageType> ABILITY_DAMAGE_TYPE;
    public static RegistryKey<DamageType> ABILITY_PIERCE_DAMAGE_TYPE;
    public static RegistryKey<DamageType> ABILITY_PROJECTILE_DAMAGE_TYPE;
    public static RegistryKey<DamageType> AUTO_TURRET_DAMAGE_TYPE;
    public static RegistryKey<DamageType> INJECTOR_DAMAGE_TYPE;
    public static RegistryKey<DamageType> FROSTBURN_DAMAGE_TYPE;
    public static RegistryKey<DamageType> GUN_SHOT_DAMAGE_TYPE;
    public static RegistryKey<DamageType> PAIN_SPLIT_DAMAGE_TYPE;
    public static RegistryKey<DamageType> PARRY_DAMAGE_TYPE;
    public static RegistryKey<DamageType> SHIELD_SHARD_DAMAGE_TYPE;
    public static RegistryKey<DamageType> SKEWER_WEAKPOINT_DAMAGE_TYPE;
    public static RegistryKey<DamageType> STOCKPILE_DAMAGE_TYPE;
    public static RegistryKey<DamageType> STUCK_SPEAR_DAMAGE_TYPE;
    public static RegistryKey<DamageType> SUMMON_WEAPON_DAMAGE_TYPE;
    public static RegistryKey<DamageType> WARPSPEED_DAMAGE_TYPE;

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

    public static DamageSource of(PlayerEntity player, RegistryKey<DamageType> key) {
        return new DamageSource(player.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), player);
    }

    public static DamageSource of(Entity player, RegistryKey<DamageType> key) {
        return new DamageSource(player.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), player);
    }

    public static DamageSource of(PlayerEntity attacker, World world, RegistryKey<DamageType> key) {
        if (attacker == null) return SoulForgeDamageTypes.of(world, key);
        return new DamageSource(attacker.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), attacker);
    }

    public static DamageSource of(Entity attacker, World world, RegistryKey<DamageType> key) {
        if (attacker == null) return SoulForgeDamageTypes.of(world, key);
        return new DamageSource(attacker.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), attacker);
    }

    public static void register() {
        ABILITY_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "ability"));
        ABILITY_PIERCE_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "ability_pierce"));
        ABILITY_PROJECTILE_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "ability_projectile"));
        AUTO_TURRET_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "auto_turret"));
        INJECTOR_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "injector"));
        FROSTBURN_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "frostburn"));
        GUN_SHOT_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "gun_shot"));
        PAIN_SPLIT_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "pain_split"));
        PARRY_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "parry"));
        SHIELD_SHARD_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "shield_shard"));
        SKEWER_WEAKPOINT_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "skewer_weakpoint"));
        STOCKPILE_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "stockpile"));
        STUCK_SPEAR_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "stuck_spear"));
        SUMMON_WEAPON_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "summon_weapon"));
        WARPSPEED_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "warpspeed"));
    }
}
