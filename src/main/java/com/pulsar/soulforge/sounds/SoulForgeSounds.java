package com.pulsar.soulforge.sounds;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoulForgeSounds {
    public static final Identifier DR_ICESHOCK = new Identifier(SoulForge.MOD_ID, "dr_iceshock");
    public static final Identifier DR_MAKE_FOUNTAIN = new Identifier(SoulForge.MOD_ID, "dr_make_fountain");
    public static final Identifier DR_REVIVAL = new Identifier(SoulForge.MOD_ID, "dr_revival");
    public static final Identifier DR_RUDEBUSTER_SWING = new Identifier(SoulForge.MOD_ID, "dr_rudebuster_swing");
    public static final Identifier BONK = new Identifier(SoulForge.MOD_ID, "bonk");
    public static final Identifier BUZZER = new Identifier(SoulForge.MOD_ID, "buzzer");
    public static final Identifier LIBMO = new Identifier(SoulForge.MOD_ID, "libmo");
    public static final Identifier FROST_WAVE = new Identifier(SoulForge.MOD_ID, "frost_wave");
    public static final Identifier SNOWGRAVE = new Identifier(SoulForge.MOD_ID, "snowgrave");
    public static final Identifier HEAL = new Identifier(SoulForge.MOD_ID, "heal");
    public static final Identifier PARRY = new Identifier(SoulForge.MOD_ID, "parry");
    public static final Identifier UT_A_BULLET = new Identifier(SoulForge.MOD_ID, "ut_a_bullet");
    public static final Identifier UT_BLASTER = new Identifier(SoulForge.MOD_ID, "ut_blaster");
    public static final Identifier UT_BOMBSPLOSION = new Identifier(SoulForge.MOD_ID, "ut_bombsplosion");
    public static final Identifier UT_EXPLOSION = new Identifier(SoulForge.MOD_ID, "ut_explosion");
    public static final Identifier UT_GRAVITY_CHANGE = new Identifier(SoulForge.MOD_ID, "ut_gravity_change");
    public static final Identifier UT_HEAL = new Identifier(SoulForge.MOD_ID, "ut_heal");
    public static final Identifier UT_LEVEL_UP = new Identifier(SoulForge.MOD_ID, "ut_levelup");
    public static final Identifier UT_REFLECT = new Identifier(SoulForge.MOD_ID, "ut_reflect");
    public static final Identifier UT_SOUL_CRACK = new Identifier(SoulForge.MOD_ID, "ut_soul_crack");
    public static final Identifier UT_SAVE = new Identifier(SoulForge.MOD_ID, "ut_save");
    public static final Identifier VINE_BOOM = new Identifier(SoulForge.MOD_ID, "vine_boom");
    public static final Identifier GUN_SHOOT = new Identifier(SoulForge.MOD_ID, "gun_shoot");
    public static final Identifier PELLET_SUMMON = new Identifier(SoulForge.MOD_ID, "pellet_summon");
    public static final Identifier SOUL_GRAB = new Identifier(SoulForge.MOD_ID, "soul_grab");
    public static final Identifier WEAPON_SUMMON = new Identifier(SoulForge.MOD_ID, "weapon_summon");
    public static final Identifier WEAPON_UNSUMMON = new Identifier(SoulForge.MOD_ID, "weapon_unsummon");
    public static final Identifier MINE_BEEP = new Identifier(SoulForge.MOD_ID, "mine_beep");
    public static final Identifier MINE_SUMMON = new Identifier(SoulForge.MOD_ID, "mine_summon");
    public static final Identifier DOMAIN_EXPANSION = new Identifier(SoulForge.MOD_ID, "domain_expansion");

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
    public static SoundEvent UT_SOUL_CRACK_EVENT = SoundEvent.of(UT_SOUL_CRACK);
    public static SoundEvent UT_SAVE_EVENT = SoundEvent.of(UT_SAVE);
    public static SoundEvent VINE_BOOM_EVENT = SoundEvent.of(VINE_BOOM);
    public static SoundEvent GUN_SHOOT_EVENT = SoundEvent.of(GUN_SHOOT);
    public static SoundEvent PELLET_SUMMON_EVENT = SoundEvent.of(PELLET_SUMMON);
    public static SoundEvent SOUL_GRAB_EVENT = SoundEvent.of(SOUL_GRAB);
    public static SoundEvent WEAPON_SUMMON_EVENT = SoundEvent.of(WEAPON_SUMMON);
    public static SoundEvent WEAPON_UNSUMMON_EVENT = SoundEvent.of(WEAPON_UNSUMMON);
    public static SoundEvent MINE_BEEP_EVENT = SoundEvent.of(MINE_BEEP);
    public static SoundEvent MINE_SUMMON_EVENT = SoundEvent.of(MINE_SUMMON);
    public static SoundEvent DOMAIN_EXPANSION_EVENT = SoundEvent.of(DOMAIN_EXPANSION);

    public static final Identifier UT_ALPHYS = new Identifier(SoulForge.MOD_ID, "ut_alphys");
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
        Registry.register(Registries.SOUND_EVENT, UT_SOUL_CRACK, UT_SOUL_CRACK_EVENT);
        Registry.register(Registries.SOUND_EVENT, UT_SAVE, UT_SAVE_EVENT);
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
