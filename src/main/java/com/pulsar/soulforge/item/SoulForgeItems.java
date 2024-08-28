package com.pulsar.soulforge.item;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.armor.CatEarsItem;
import com.pulsar.soulforge.armor.PlatformBootsItem;
import com.pulsar.soulforge.item.devices.devices.*;
import com.pulsar.soulforge.item.devices.machines.*;
import com.pulsar.soulforge.item.devices.trinkets.*;
import com.pulsar.soulforge.item.special.*;
import com.pulsar.soulforge.item.weapons.*;
import com.pulsar.soulforge.item.weapons.weapon_wheel.*;
import com.pulsar.soulforge.trait.TraitBase;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SoulForgeItems {
    public static Item RegisterItem(String id, FabricItemSettings settings) {
        Item item = Registry.register(Registries.ITEM, new Identifier(SoulForge.MOD_ID, id), new Item(settings));
        addToItemGroup(item);
        return item;
    }

    public static Item RegisterItem(String id, Item item) {
        Item registered = Registry.register(Registries.ITEM, new Identifier(SoulForge.MOD_ID, id), item);
        addToItemGroup(registered);
        return registered;
    }
    public static Item RegisterItem(String id, FabricItemSettings settings, boolean addToGroup) {
        Item item = Registry.register(Registries.ITEM, new Identifier(SoulForge.MOD_ID, id), new Item(settings));
        if (addToGroup) addToItemGroup(item);
        return item;
    }

    public static Item RegisterItem(String id, Item item, boolean addToGroup) {
        Item registered = Registry.register(Registries.ITEM, new Identifier(SoulForge.MOD_ID, id), item);
        if (addToGroup) addToItemGroup(registered);
        return registered;
    }

    public static void addToItemGroup(Item item) {
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(item));
    }

    public static Item BRAVERY_ESSENCE;
    public static Item JUSTICE_ESSENCE;
    public static Item KINDNESS_ESSENCE;
    public static Item PATIENCE_ESSENCE;
    public static Item INTEGRITY_ESSENCE;
    public static Item PERSEVERANCE_ESSENCE;
    public static Item DETERMINATION_ESSENCE;
    public static Item ARNICITE;
    public static Item BRAVERY_ARNICITE;
    public static Item JUSTICE_ARNICITE;
    public static Item KINDNESS_ARNICITE;
    public static Item PATIENCE_ARNICITE;
    public static Item INTEGRITY_ARNICITE;
    public static Item PERSEVERANCE_ARNICITE;
    public static Item DETERMINATION_ARNICITE;
    public static Item ARNICITE_HEART;
    public static Item BRAVERY_ARNICITE_HEART;
    public static Item JUSTICE_ARNICITE_HEART;
    public static Item KINDNESS_ARNICITE_HEART;
    public static Item PATIENCE_ARNICITE_HEART;
    public static Item INTEGRITY_ARNICITE_HEART;
    public static Item PERSEVERANCE_ARNICITE_HEART;
    public static Item DETERMINATION_ARNICITE_HEART;
    public static Item ARNICITE_CORE;
    public static Item BRAVERY_ARNICITE_CORE;
    public static Item JUSTICE_ARNICITE_CORE;
    public static Item KINDNESS_ARNICITE_CORE;
    public static Item PATIENCE_ARNICITE_CORE;
    public static Item INTEGRITY_ARNICITE_CORE;
    public static Item PERSEVERANCE_ARNICITE_CORE;
    public static Item DETERMINATION_ARNICITE_CORE;
    public static Item SOUL;

    public static Item SHOTGUN_FIST;
    public static Item JUSTICE_BOW;
    public static Item JUSTICE_CROSSBOW;
    public static Item JUSTICE_REVOLVER;

    public static Item FLAMETHROWER;
    public static Item BRAVERY_SPEAR;
    public static Item BRAVERY_GAUNTLETS;
    public static Item BRAVERY_HAMMER;

    public static Item KINDNESS_SHIELD;

    public static Item FREEZE_RING;
    public static Item FROST_WAVE;

    public static Item INTEGRITY_RAPIER;

    public static Item PERSEVERANCE_BLADES;
    public static Item PERSEVERANCE_EDGE;
    public static Item PERSEVERANCE_CLAW;
    public static Item PERSEVERANCE_HARPOON;
    public static Item COLOSSAL_CLAYMORE_DISPLAY;
    public static Item COLOSSAL_CLAYMORE;

    // weapon wheel
    public static Item DETERMINATION_BLADES;
    public static Item DETERMINATION_BOW;
    public static Item DETERMINATION_CLAW;
    public static Item DETERMINATION_CROSSBOW;
    public static Item DETERMINATION_EDGE;
    public static Item DETERMINATION_HARPOON;
    public static Item DETERMINATION_GAUNTLETS;
    public static Item DETERMINATION_GREATSWORD;
    public static Item DETERMINATION_GUN;
    public static Item DETERMINATION_HAMMER;
    public static Item DETERMINATION_RAPIER;
    public static Item DETERMINATION_SHIELD;
    public static Item DETERMINATION_SPEAR;
    public static Item DETERMINATION_STAFF;
    public static Item DETERMINATION_SWORD;
    public static Item REAL_KNIFE;
    public static Item DETERMINATION_CLAYMORE_DISPLAY;

    public static Item BFRCMG;
    public static Item LIGHTNING_ROD;
    public static Item FROSTBITE_ROUND;
    public static Item CRUSHING_ROUND;
    public static Item PUNCTURING_ROUND;
    public static Item SUPPRESSING_ROUND;
    public static Item GUNBLADES;
    public static Item MUSKET_BLADE;
    public static Item GUNLANCE;
    public static Item JUSTICE_HARPOON;
    public static Item TRICK_ANCHOR;

    public static Item ANTLER;
    public static Item BETE_NOIRE;
    public static Item RESPONDING_FREENIX;
    public static Item NEBULOUS_BREAD;
    public static Item BURNT_PAN;
    public static Item CARDBOARD_TUBE;
    public static Item FLAME_SLIME;
    public static Item CAT_EARS;
    public static Item ENCYCLOPEDIA;

    public static Item ARNICITE_GEODE;
    public static Item SIPHON_TEMPLATE;

    public static Item DOME_EMITTER;
    public static Item WARP_DIAMOND;
    public static Item INCENDIARY_GRENADE;
    public static Item ANTIHEAL_DART;
    public static Item JUSTICE_ARROW;

    public static Item REVIVAL_IDOL;
    public static Item HEAL_TABLET;
    public static Item FREEZE_RAY;
    public static Item PLATFORM_BOOTS;
    public static Item SHATTERDRILL;
    public static Item GRAPPLE_HOOK;
    public static Item JUSTICE_GUN;
    public static Item SOUL_JAR;

    public static Item DT_INJECTOR;
    public static Item RECALL_STOPWATCH;
    public static Item DETONATOR;
    public static Item RAILKILLER;
    public static Item SIPHON_IMBUER;

    public static Item BRAVERY_GUMMY;
    public static Item JUSTICE_GUMMY;
    public static Item KINDNESS_GUMMY;
    public static Item PATIENCE_GUMMY;
    public static Item INTEGRITY_GUMMY;
    public static Item PERSEVERANCE_GUMMY;
    public static Item DETERMINATION_GUMMY;

    public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(SoulForge.MOD_ID, "item_group"));

    public static void registerItems() {
        SOUL = RegisterItem("soul", new FabricItemSettings(), false);

        INTEGRITY_ESSENCE = RegisterItem("integrity_essence", new FabricItemSettings());
        KINDNESS_ESSENCE = RegisterItem("kindness_essence", new FabricItemSettings());
        PATIENCE_ESSENCE = RegisterItem("patience_essence", new FabricItemSettings());
        JUSTICE_ESSENCE = RegisterItem("justice_essence", new FabricItemSettings());
        BRAVERY_ESSENCE = RegisterItem("bravery_essence", new FabricItemSettings());
        PERSEVERANCE_ESSENCE = RegisterItem("perseverance_essence", new FabricItemSettings());
        DETERMINATION_ESSENCE = RegisterItem("determination_essence", new FabricItemSettings());
        ARNICITE = RegisterItem("arnicite", new ArniciteItem());
        INTEGRITY_ARNICITE = RegisterItem("integrity_arnicite", new TraitedArniciteItem(Traits.integrity));
        KINDNESS_ARNICITE = RegisterItem("kindness_arnicite", new TraitedArniciteItem(Traits.kindness));
        PATIENCE_ARNICITE = RegisterItem("patience_arnicite", new TraitedArniciteItem(Traits.patience));
        JUSTICE_ARNICITE = RegisterItem("justice_arnicite", new TraitedArniciteItem(Traits.justice));
        BRAVERY_ARNICITE = RegisterItem("bravery_arnicite", new TraitedArniciteItem(Traits.bravery));
        PERSEVERANCE_ARNICITE = RegisterItem("perseverance_arnicite", new TraitedArniciteItem(Traits.perseverance));
        DETERMINATION_ARNICITE = RegisterItem("determination_arnicite", new TraitedArniciteItem(Traits.determination));
        ARNICITE_HEART = RegisterItem("arnicite_heart", new ArniciteHeartItem());
        INTEGRITY_ARNICITE_HEART = RegisterItem("integrity_arnicite_heart", new TraitedArniciteHeartItem(Traits.integrity));
        PERSEVERANCE_ARNICITE_HEART = RegisterItem("perseverance_arnicite_heart", new TraitedArniciteHeartItem(Traits.perseverance));
        PATIENCE_ARNICITE_HEART = RegisterItem("patience_arnicite_heart", new TraitedArniciteHeartItem(Traits.patience));
        KINDNESS_ARNICITE_HEART = RegisterItem("kindness_arnicite_heart", new TraitedArniciteHeartItem(Traits.kindness));
        JUSTICE_ARNICITE_HEART = RegisterItem("justice_arnicite_heart", new TraitedArniciteHeartItem(Traits.justice));
        BRAVERY_ARNICITE_HEART = RegisterItem("bravery_arnicite_heart", new TraitedArniciteHeartItem(Traits.bravery));
        DETERMINATION_ARNICITE_HEART = RegisterItem("determination_arnicite_heart", new TraitedArniciteHeartItem(Traits.determination));
        ARNICITE_CORE = RegisterItem("arnicite_core", new ArniciteCoreItem());
        BRAVERY_ARNICITE_CORE = RegisterItem("bravery_arnicite_core", new TraitedArniciteCoreItem(Traits.bravery));
        JUSTICE_ARNICITE_CORE = RegisterItem("justice_arnicite_core", new TraitedArniciteCoreItem(Traits.justice));
        KINDNESS_ARNICITE_CORE = RegisterItem("kindness_arnicite_core", new TraitedArniciteCoreItem(Traits.kindness));
        PATIENCE_ARNICITE_CORE = RegisterItem("patience_arnicite_core", new TraitedArniciteCoreItem(Traits.patience));
        INTEGRITY_ARNICITE_CORE = RegisterItem("integrity_arnicite_core", new TraitedArniciteCoreItem(Traits.integrity));
        PERSEVERANCE_ARNICITE_CORE = RegisterItem("perseverance_arnicite_core", new TraitedArniciteCoreItem(Traits.perseverance));
        DETERMINATION_ARNICITE_CORE = RegisterItem("determination_arnicite_core", new TraitedArniciteCoreItem(Traits.determination));

        SHOTGUN_FIST = RegisterItem("shotgun_fist", new ShotgunFist());
        JUSTICE_BOW = RegisterItem("justice_bow", new JusticeBow());
        JUSTICE_CROSSBOW = RegisterItem("justice_crossbow", new JusticeCrossbow());
        JUSTICE_REVOLVER = RegisterItem("justice_revolver", new JusticeRevolver());

        FLAMETHROWER = RegisterItem("flamethrower", new Flamethrower());
        BRAVERY_SPEAR = RegisterItem("bravery_spear", new BraverySpear());
        BRAVERY_GAUNTLETS = RegisterItem("bravery_gauntlets", new BraveryGauntlets());
        BRAVERY_HAMMER = RegisterItem("bravery_hammer", new BraveryHammer());

        KINDNESS_SHIELD = RegisterItem("kindness_shield", new KindnessShield());

        FREEZE_RING = RegisterItem("freeze_ring", new FreezeRing());
        FROST_WAVE = RegisterItem("frost_wave", new FrostWave());

        INTEGRITY_RAPIER = RegisterItem("integrity_rapier", new IntegrityRapier());

        PERSEVERANCE_BLADES = RegisterItem("perseverance_blades", new PerseveranceBlades());
        PERSEVERANCE_EDGE = RegisterItem("perseverance_edge", new PerseveranceEdge());
        PERSEVERANCE_CLAW = RegisterItem("perseverance_claw", new PerseveranceClaw());
        PERSEVERANCE_HARPOON = RegisterItem("perseverance_harpoon", new PerseveranceHarpoon());
        COLOSSAL_CLAYMORE_DISPLAY = RegisterItem("colossal_claymore_display", new FabricItemSettings().maxCount(1));
        COLOSSAL_CLAYMORE = RegisterItem("colossal_claymore", new ColossalClaymore());

        DETERMINATION_BLADES = RegisterItem("determination_blades", new DeterminationBlades());
        DETERMINATION_BOW = RegisterItem("determination_bow", new DeterminationBow());
        DETERMINATION_CLAW = RegisterItem("determination_claw", new DeterminationClaw());
        DETERMINATION_CROSSBOW = RegisterItem("determination_crossbow", new DeterminationCrossbow());
        DETERMINATION_EDGE = RegisterItem("determination_edge", new DeterminationEdge());
        DETERMINATION_HARPOON = RegisterItem("determination_harpoon", new DeterminationHarpoon());
        DETERMINATION_GAUNTLETS = RegisterItem("determination_gauntlets", new DeterminationGauntlets());
        DETERMINATION_GREATSWORD = RegisterItem("determination_greatsword", new DeterminationGreatsword());
        DETERMINATION_GUN = RegisterItem("determination_gun", new DeterminationGun());
        DETERMINATION_HAMMER = RegisterItem("determination_hammer", new DeterminationHammer());
        DETERMINATION_RAPIER = RegisterItem("determination_rapier", new DeterminationRapier());
        DETERMINATION_SHIELD = RegisterItem("determination_shield", new DeterminationShield());
        DETERMINATION_SPEAR = RegisterItem("determination_spear", new DeterminationSpear());
        DETERMINATION_STAFF = RegisterItem("determination_staff", new DeterminationStaff());
        DETERMINATION_SWORD = RegisterItem("determination_sword", new DeterminationSword());
        REAL_KNIFE = RegisterItem("real_knife", new RealKnife());
        DETERMINATION_CLAYMORE_DISPLAY = RegisterItem("determination_claymore_display", new FabricItemSettings().maxCount(1));

        BFRCMG = RegisterItem("bfrcmg", new BFRCMG());
        LIGHTNING_ROD = RegisterItem("lightning_rod", new LightningRod());
        FROSTBITE_ROUND = RegisterItem("frostbite_round", new FabricItemSettings());
        CRUSHING_ROUND = RegisterItem("crushing_round", new FabricItemSettings());
        PUNCTURING_ROUND = RegisterItem("puncturing_round", new FabricItemSettings());
        SUPPRESSING_ROUND = RegisterItem("suppressing_round", new FabricItemSettings());
        GUNBLADES = RegisterItem("gunblades", new Gunblades());
        MUSKET_BLADE = RegisterItem("musket_blade", new MusketBlade());
        GUNLANCE = RegisterItem("gunlance", new Gunlance());
        JUSTICE_HARPOON = RegisterItem("justice_harpoon", new JusticeHarpoon());
        TRICK_ANCHOR = RegisterItem("trick_anchor", new TrickAnchor());

        ANTLER = RegisterItem("antler", new AntlerItem());
        BETE_NOIRE = RegisterItem("bete_noire", new BeteNoire());
        RESPONDING_FREENIX = RegisterItem("responding_freenix", new RespondingFreenix());
        NEBULOUS_BREAD = RegisterItem("nebulous_bread", new NebulousBread());
        BURNT_PAN = RegisterItem("burnt_pan", new BurntPan());
        CARDBOARD_TUBE = RegisterItem("cardboard_tube", new CardboardTube());
        FLAME_SLIME = RegisterItem("flame_slime", new FlameSlime());
        CAT_EARS = RegisterItem("cat_ears", new CatEarsItem(), false);
        ENCYCLOPEDIA = RegisterItem("encyclopedia", new EncyclopediaItem());

        ARNICITE_GEODE = RegisterItem("arnicite_geode", new ArniciteGeode());
        SIPHON_TEMPLATE = RegisterItem("siphon_template", new FabricItemSettings());

        // devices
        DOME_EMITTER = RegisterItem("dome_emitter", new DomeEmitter());
        WARP_DIAMOND = RegisterItem("warp_diamond", new WarpDiamond());
        INCENDIARY_GRENADE = RegisterItem("incendiary_grenade", new IncendiaryGrenade());
        JUSTICE_ARROW = RegisterItem("justice_arrow", new JusticeArrowItem());
        ANTIHEAL_DART = RegisterItem("antiheal_dart", new AntihealDart());
        SOUL_JAR = RegisterItem("soul_jar", new SoulJarItem());

        REVIVAL_IDOL = RegisterItem("revival_idol", new RevivalIdol());
        SHATTERDRILL = RegisterItem("shatterdrill", new Shatterdrill());
        HEAL_TABLET = RegisterItem("heal_tablet", new HealTablet());
        FREEZE_RAY = RegisterItem("freeze_ray", new FreezeRay());
        PLATFORM_BOOTS = RegisterItem("platform_boots", new PlatformBootsItem());
        JUSTICE_GUN = RegisterItem("justice_gun", new JusticeGun());
        GRAPPLE_HOOK = RegisterItem("grapple_hook", new GrappleHook());

        DT_INJECTOR = RegisterItem("determination_injector", new DeterminationInjector());
        DETONATOR = RegisterItem("detonator", new Detonator());
        RECALL_STOPWATCH = RegisterItem("recall_stopwatch", new RecallStopwatch());
        SIPHON_IMBUER = RegisterItem("siphon_imbuer", new SiphonImbuer());
        RAILKILLER = RegisterItem("railkiller", new Railkiller());

        BRAVERY_GUMMY = RegisterItem("bravery_gummy", new FabricItemSettings().food(new FoodComponent.Builder().hunger(1).snack().build()));
        JUSTICE_GUMMY = RegisterItem("justice_gummy", new FabricItemSettings().food(new FoodComponent.Builder().hunger(1).snack().build()));
        KINDNESS_GUMMY = RegisterItem("kindness_gummy", new FabricItemSettings().food(new FoodComponent.Builder().hunger(1).snack().build()));
        PATIENCE_GUMMY = RegisterItem("patience_gummy", new FabricItemSettings().food(new FoodComponent.Builder().hunger(1).snack().build()));
        INTEGRITY_GUMMY = RegisterItem("integrity_gummy", new FabricItemSettings().food(new FoodComponent.Builder().hunger(1).snack().build()));
        PERSEVERANCE_GUMMY = RegisterItem("perseverance_gummy", new FabricItemSettings().food(new FoodComponent.Builder().hunger(1).snack().build()));
        DETERMINATION_GUMMY = RegisterItem("determination_gummy", new FabricItemSettings().food(new FoodComponent.Builder().hunger(2).snack().build()));

        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder()
                .icon(() -> new ItemStack(DETERMINATION_ARNICITE_HEART))
                .displayName(Text.translatable("itemGroup.soulforge.item_group"))
                .build());
    }

    public static ItemStack getSoulItem(TraitBase trait1, TraitBase trait2) {
        String trait1str = trait1.getName();
        String trait2str = trait2.getName();
        ItemStack stack = new ItemStack(SOUL);
        stack.getOrCreateNbt().putString("trait1", trait1str);
        stack.getOrCreateNbt().putString("trait2", trait2str);
        return stack;
    }
}
