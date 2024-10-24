package com.pulsar.soulforge.tag;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class SoulForgeTags {
    public static final TagKey<Item> ARTIFACT_SIPHONABLE = TagKey.of(RegistryKeys.ITEM, new Identifier(SoulForge.MOD_ID, "artifact_siphonable"));
    public static final TagKey<Item> IMBUER_SWORDS = TagKey.of(RegistryKeys.ITEM, new Identifier(SoulForge.MOD_ID, "imbuer_swords"));
    public static final TagKey<Item> IMBUER_SHIELDS = TagKey.of(RegistryKeys.ITEM, new Identifier(SoulForge.MOD_ID, "imbuer_shields"));
    public static final TagKey<Item> IMBUER_AXES = TagKey.of(RegistryKeys.ITEM, new Identifier(SoulForge.MOD_ID, "imbuer_axes"));
    public static final TagKey<Item> IMBUER_CROSSBOWS = TagKey.of(RegistryKeys.ITEM, new Identifier(SoulForge.MOD_ID, "imbuer_crossbows"));
    public static final TagKey<Item> IMBUER_TRIDENTS = TagKey.of(RegistryKeys.ITEM, new Identifier(SoulForge.MOD_ID, "imbuer_tridents"));
    public static final TagKey<Item> IMBUER_BOWS = TagKey.of(RegistryKeys.ITEM, new Identifier(SoulForge.MOD_ID, "imbuer_bows"));
    public static final TagKey<Item> EFFECTIVE_LV_WEAPON = TagKey.of(RegistryKeys.ITEM, new Identifier(SoulForge.MOD_ID, "effective_lv_weapon"));
    public static final TagKey<Item> SHIELDS = TagKey.of(RegistryKeys.ITEM, new Identifier(SoulForge.MOD_ID, "shields"));
    public static final TagKey<Item> BREAKS_SHIELD = TagKey.of(RegistryKeys.ITEM, new Identifier(SoulForge.MOD_ID, "breaks_shield"));
    public static final TagKey<Item> SIPHON_ADDITION = TagKey.of(RegistryKeys.ITEM, new Identifier(SoulForge.MOD_ID, "siphon_addition"));
    public static final TagKey<Item> SIPHONABLE = TagKey.of(RegistryKeys.ITEM, new Identifier(SoulForge.MOD_ID, "siphonable"));
    public static final TagKey<Item> PARRY_ITEMS = TagKey.of(RegistryKeys.ITEM, new Identifier(SoulForge.MOD_ID, "parry_items"));
    public static final TagKey<DamageType> NO_ARMOR_BREAK = TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(SoulForge.MOD_ID, "no_armor_break"));
    public static final TagKey<EntityType<?>> BOSS_ENTITY = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "boss_entity"));
    public static final TagKey<EntityType<?>> HATE_ALLY = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "hate_ally"));
}
