package com.pulsar.soulforge.damage_type;

import com.pulsar.soulforge.SoulForge;
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
    public static RegistryKey<DamageType> STUCK_SPEAR_DAMAGE_TYPE;
    public static RegistryKey<DamageType> KNIFE_DAMAGE_TYPE;
    public static RegistryKey<DamageType> GUN_SHOT_DAMAGE_TYPE;
    public static RegistryKey<DamageType> MISSILE_DAMAGE_TYPE;
    public static RegistryKey<DamageType> WARPSPEED_DAMAGE_TYPE;
    public static RegistryKey<DamageType> PAIN_SPLIT_DAMAGE_TYPE;

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

    /*public static DamageSource of(PlayerEntity attacker, RegistryKey<DamageType> key) {
        return new DamageSource(attacker.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), attacker);
    }*/

    public static void register() {
        ABILITY_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(SoulForge.MOD_ID, "ability"));
        ABILITY_PIERCE_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(SoulForge.MOD_ID, "ability_pierce"));
        STUCK_SPEAR_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(SoulForge.MOD_ID, "stuck_spear"));
        KNIFE_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(SoulForge.MOD_ID, "knife"));
        GUN_SHOT_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(SoulForge.MOD_ID, "gun_shot"));
        MISSILE_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(SoulForge.MOD_ID, "missile"));
        WARPSPEED_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(SoulForge.MOD_ID, "warpspeed"));
        PAIN_SPLIT_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(SoulForge.MOD_ID, "pain_split"));
    }
}
