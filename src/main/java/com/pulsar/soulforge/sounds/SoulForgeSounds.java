package com.pulsar.soulforge.sounds;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoulForgeSounds {
    public static final Identifier DR_ICESHOCK = Identifier.of(SoulForge.MOD_ID, "dr_iceshock");
    public static final Identifier DR_MAKE_FOUNTAIN = Identifier.of(SoulForge.MOD_ID, "dr_make_fountain");
    public static final Identifier DR_REVIVAL = Identifier.of(SoulForge.MOD_ID, "dr_revival");
    public static final Identifier DR_RUDEBUSTER_SWING = Identifier.of(SoulForge.MOD_ID, "dr_rudebuster_swing");
    public static final Identifier BONK = Identifier.of(SoulForge.MOD_ID, "bonk");
    public static final Identifier BUZZER = Identifier.of(SoulForge.MOD_ID, "buzzer");
    public static final Identifier LIBMO = Identifier.of(SoulForge.MOD_ID, "libmo");
    public static final Identifier FROST_WAVE = Identifier.of(SoulForge.MOD_ID, "frost_wave");
    public static final Identifier SNOWGRAVE = Identifier.of(SoulForge.MOD_ID, "snowgrave");
    public static final Identifier HEAL = Identifier.of(SoulForge.MOD_ID, "heal");
    public static final Identifier PARRY = Identifier.of(SoulForge.MOD_ID, "parry");
    public static final Identifier UT_A_BULLET = Identifier.of(SoulForge.MOD_ID, "ut_a_bullet");
    public static final Identifier UT_BLASTER = Identifier.of(SoulForge.MOD_ID, "ut_blaster");
    public static final Identifier UT_BOMBSPLOSION = Identifier.of(SoulForge.MOD_ID, "ut_bombsplosion");
    public static final Identifier UT_EXPLOSION = Identifier.of(SoulForge.MOD_ID, "ut_explosion");
    public static final Identifier UT_GRAVITY_CHANGE = Identifier.of(SoulForge.MOD_ID, "ut_gravity_change");
    public static final Identifier UT_HEAL = Identifier.of(SoulForge.MOD_ID, "ut_heal");
    public static final Identifier UT_LEVEL_UP = Identifier.of(SoulForge.MOD_ID, "ut_levelup");
    public static final Identifier UT_REFLECT = Identifier.of(SoulForge.MOD_ID, "ut_reflect");
    public static final Identifier VINE_BOOM = Identifier.of(SoulForge.MOD_ID, "vine_boom");
    public static final Identifier GUN_SHOOT = Identifier.of(SoulForge.MOD_ID, "gun_shoot");
    public static final Identifier PELLET_SUMMON = Identifier.of(SoulForge.MOD_ID, "pellet_summon");
    public static final Identifier SOUL_GRAB = Identifier.of(SoulForge.MOD_ID, "soul_grab");
    public static final Identifier WEAPON_SUMMON = Identifier.of(SoulForge.MOD_ID, "weapon_summon");
    public static final Identifier WEAPON_UNSUMMON = Identifier.of(SoulForge.MOD_ID, "weapon_unsummon");
    public static final Identifier MINE_BEEP = Identifier.of(SoulForge.MOD_ID, "mine_beep");
    public static final Identifier MINE_SUMMON = Identifier.of(SoulForge.MOD_ID, "mine_summon");
    public static final Identifier DOMAIN_EXPANSION = Identifier.of(SoulForge.MOD_ID, "domain_expansion");

    public static SoundEvent DR_ICESHOCK_EVENT = SoundEvent.of(DR_ICESHOCK);
    public static SoundEvent DR_MAKE_FOUNTAIN_EVENT = SoundEvent.of(DR_MAKE_FOUNTAIN);
    public static SoundEvent DR_REVIVAL_EVENT = SoundEvent.of(DR_REVIVAL);
    public static SoundEvent DR_RUDEBUSTER_SWING_EVENT = SoundEvent.of(DR_RUDEBUSTER_SWING);
    public static SoundEvent BONK_EVENT = SoundEvent.of(BONK);
    public static SoundEvent BUZZER_EVENT = SoundEvent.of(BUZZER);
    public static SoundEvent LIBMO_EVENT = SoundEvent.of(LIBMO);
    public static SoundEvent FROST_WAVE_EVENT = SoundEvent.of(FROST_WAVE);
    public static SoundEvent SNOWGRAVE_EVENT = SoundEvent.of(SNOWGRAVE);
    public static SoundEvent HEAL_EVENT = SoundEvent.of(HEAL);
    public static SoundEvent PARRY_EVENT = SoundEvent.of(PARRY);
    public static SoundEvent UT_A_BULLET_EVENT = SoundEvent.of(UT_A_BULLET);
    public static SoundEvent UT_BLASTER_EVENT = SoundEvent.of(UT_BLASTER);
    public static SoundEvent UT_BOMBSPLOSION_EVENT = SoundEvent.of(UT_BOMBSPLOSION);
    public static SoundEvent UT_EXPLOSION_EVENT = SoundEvent.of(UT_EXPLOSION);
    public static SoundEvent UT_GRAVITY_CHANGE_EVENT = SoundEvent.of(UT_GRAVITY_CHANGE);
    public static SoundEvent UT_HEAL_EVENT = SoundEvent.of(UT_HEAL);
    public static SoundEvent UT_LEVEL_UP_EVENT = SoundEvent.of(UT_LEVEL_UP);
    public static SoundEvent UT_REFLECT_EVENT = SoundEvent.of(UT_REFLECT);
    public static SoundEvent VINE_BOOM_EVENT = SoundEvent.of(VINE_BOOM);
    public static SoundEvent GUN_SHOOT_EVENT = SoundEvent.of(GUN_SHOOT);
    public static SoundEvent PELLET_SUMMON_EVENT = SoundEvent.of(PELLET_SUMMON);
    public static SoundEvent SOUL_GRAB_EVENT = SoundEvent.of(SOUL_GRAB);
    public static SoundEvent WEAPON_SUMMON_EVENT = SoundEvent.of(WEAPON_SUMMON);
    public static SoundEvent WEAPON_UNSUMMON_EVENT = SoundEvent.of(WEAPON_UNSUMMON);
    public static SoundEvent MINE_BEEP_EVENT = SoundEvent.of(MINE_BEEP);
    public static SoundEvent MINE_SUMMON_EVENT = SoundEvent.of(MINE_SUMMON);
    public static SoundEvent DOMAIN_EXPANSION_EVENT = SoundEvent.of(DOMAIN_EXPANSION);

    public static final Identifier UT_ALPHYS = Identifier.of(SoulForge.MOD_ID, "ut_alphys");
    public static SoundEvent UT_ALPHYS_EVENT = SoundEvent.of(UT_ALPHYS);

    public static void registerSounds() {
        Registry.register(Registries.SOUND_EVENT, DR_ICESHOCK, DR_ICESHOCK_EVENT);
        Registry.register(Registries.SOUND_EVENT, DR_MAKE_FOUNTAIN, DR_MAKE_FOUNTAIN_EVENT);
        Registry.register(Registries.SOUND_EVENT, DR_REVIVAL, DR_REVIVAL_EVENT);
        Registry.register(Registries.SOUND_EVENT, DR_RUDEBUSTER_SWING, DR_RUDEBUSTER_SWING_EVENT);
        Registry.register(Registries.SOUND_EVENT, BONK, BONK_EVENT);
        Registry.register(Registries.SOUND_EVENT, BUZZER, BUZZER_EVENT);
        Registry.register(Registries.SOUND_EVENT, LIBMO, LIBMO_EVENT);
        Registry.register(Registries.SOUND_EVENT, FROST_WAVE, FROST_WAVE_EVENT);
        Registry.register(Registries.SOUND_EVENT, SNOWGRAVE, SNOWGRAVE_EVENT);
        Registry.register(Registries.SOUND_EVENT, HEAL, HEAL_EVENT);
        Registry.register(Registries.SOUND_EVENT, PARRY, PARRY_EVENT);
        Registry.register(Registries.SOUND_EVENT, UT_A_BULLET, UT_A_BULLET_EVENT);
        Registry.register(Registries.SOUND_EVENT, UT_BLASTER, UT_BLASTER_EVENT);
        Registry.register(Registries.SOUND_EVENT, UT_BOMBSPLOSION, UT_BOMBSPLOSION_EVENT);
        Registry.register(Registries.SOUND_EVENT, UT_EXPLOSION, UT_EXPLOSION_EVENT);
        Registry.register(Registries.SOUND_EVENT, UT_GRAVITY_CHANGE, UT_GRAVITY_CHANGE_EVENT);
        Registry.register(Registries.SOUND_EVENT, UT_HEAL, UT_HEAL_EVENT);
        Registry.register(Registries.SOUND_EVENT, UT_LEVEL_UP, UT_LEVEL_UP_EVENT);
        Registry.register(Registries.SOUND_EVENT, UT_REFLECT, UT_REFLECT_EVENT);
        Registry.register(Registries.SOUND_EVENT, VINE_BOOM, VINE_BOOM_EVENT);
        Registry.register(Registries.SOUND_EVENT, GUN_SHOOT, GUN_SHOOT_EVENT);
        Registry.register(Registries.SOUND_EVENT, PELLET_SUMMON, PELLET_SUMMON_EVENT);
        Registry.register(Registries.SOUND_EVENT, SOUL_GRAB, SOUL_GRAB_EVENT);
        Registry.register(Registries.SOUND_EVENT, WEAPON_SUMMON, WEAPON_SUMMON_EVENT);
        Registry.register(Registries.SOUND_EVENT, WEAPON_UNSUMMON, WEAPON_UNSUMMON_EVENT);
        Registry.register(Registries.SOUND_EVENT, MINE_BEEP, MINE_BEEP_EVENT);
        Registry.register(Registries.SOUND_EVENT, DOMAIN_EXPANSION, DOMAIN_EXPANSION_EVENT);

        Registry.register(Registries.SOUND_EVENT, UT_ALPHYS, UT_ALPHYS_EVENT);
    }
}
