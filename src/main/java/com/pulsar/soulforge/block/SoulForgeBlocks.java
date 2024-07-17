package com.pulsar.soulforge.block;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class SoulForgeBlocks {
    public static Block RegisterBlock(String id, FabricBlockSettings settings) {
        return Registry.register(Registries.BLOCK, new Identifier(SoulForge.MOD_ID, id), new Block(settings));
    }
    public static Block RegisterBlock(String id, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(SoulForge.MOD_ID, id), block);
    }

    public static Item RegisterBlockItem(String id, Block block, FabricItemSettings settings) {
        Item registered = Registry.register(Registries.ITEM, new Identifier(SoulForge.MOD_ID, id), new BlockItem(block, settings));
        SoulForgeItems.addToItemGroup(registered);
        return registered;
    }

    public static Item RegisterBlockItem(String id, Item item) {
        Item registered = Registry.register(Registries.ITEM, new Identifier(SoulForge.MOD_ID, id), item);
        SoulForgeItems.addToItemGroup(registered);
        return registered;
    }
    
    public static BlockSoundGroup ARNICITE_BLOCK_SOUNDS = new BlockSoundGroup(1.0F, 0.5F, SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, SoundEvents.BLOCK_AMETHYST_BLOCK_PLACE, SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundEvents.BLOCK_AMETHYST_BLOCK_FALL);

    public static Block SOUL_FORGE_BLOCK;
    public static Item SOUL_FORGE_ITEM;
    public static BlockEntityType<SoulForgeBlockEntity> SOUL_FORGE_BLOCK_ENTITY;
    public static Block DOME_BLOCK;
    public static Block DETERMINATION_DOME_BLOCK;
    public static Block CREATIVE_ZONE;
    public static Item CREATIVE_ZONE_ITEM;
    public static BlockEntityType<CreativeZoneBlockEntity> CREATIVE_ZONE_ENTITY;
    public static Block SOUL_JAR;
    public static BlockEntityType<SoulJarBlockEntity> SOUL_JAR_BLOCK_ENTITY;

    public static Block ARNICITE_BLOCK;
    public static Block CUT_ARNICITE_BLOCK;
    public static Item ARNICITE_BLOCK_ITEM;
    public static Item CUT_ARNICITE_BLOCK_ITEM;
    public static Block DETERMINATION_ARNICITE_BLOCK;
    public static Block CUT_DETERMINATION_ARNICITE_BLOCK;
    public static Item DETERMINATION_ARNICITE_BLOCK_ITEM;
    public static Item CUT_DETERMINATION_ARNICITE_BLOCK_ITEM;
    public static Block BRAVERY_ARNICITE_BLOCK;
    public static Block CUT_BRAVERY_ARNICITE_BLOCK;
    public static Item BRAVERY_ARNICITE_BLOCK_ITEM;
    public static Item CUT_BRAVERY_ARNICITE_BLOCK_ITEM;
    public static Block JUSTICE_ARNICITE_BLOCK;
    public static Block CUT_JUSTICE_ARNICITE_BLOCK;
    public static Item JUSTICE_ARNICITE_BLOCK_ITEM;
    public static Item CUT_JUSTICE_ARNICITE_BLOCK_ITEM;
    public static Block KINDNESS_ARNICITE_BLOCK;
    public static Block CUT_KINDNESS_ARNICITE_BLOCK;
    public static Item KINDNESS_ARNICITE_BLOCK_ITEM;
    public static Item CUT_KINDNESS_ARNICITE_BLOCK_ITEM;
    public static Block PATIENCE_ARNICITE_BLOCK;
    public static Block CUT_PATIENCE_ARNICITE_BLOCK;
    public static Item PATIENCE_ARNICITE_BLOCK_ITEM;
    public static Item CUT_PATIENCE_ARNICITE_BLOCK_ITEM;
    public static Block INTEGRITY_ARNICITE_BLOCK;
    public static Block CUT_INTEGRITY_ARNICITE_BLOCK;
    public static Item INTEGRITY_ARNICITE_BLOCK_ITEM;
    public static Item CUT_INTEGRITY_ARNICITE_BLOCK_ITEM;
    public static Block PERSEVERANCE_ARNICITE_BLOCK;
    public static Block CUT_PERSEVERANCE_ARNICITE_BLOCK;
    public static Item PERSEVERANCE_ARNICITE_BLOCK_ITEM;
    public static Item CUT_PERSEVERANCE_ARNICITE_BLOCK_ITEM;

    public static void registerBlocks() {
        SOUL_FORGE_BLOCK = RegisterBlock("soul_forge_block", new SoulForgeBlock());
        SOUL_FORGE_ITEM = RegisterBlockItem("soul_forge_item", SOUL_FORGE_BLOCK, new FabricItemSettings());
        SOUL_FORGE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "soul_forge_block_entity"), FabricBlockEntityTypeBuilder.create(SoulForgeBlockEntity::new, SOUL_FORGE_BLOCK).build());
        DOME_BLOCK = RegisterBlock("dome_block", new DomeBlock());
        DETERMINATION_DOME_BLOCK = RegisterBlock("determination_dome_block", new DeterminationDomeBlock());
        CREATIVE_ZONE = RegisterBlock("creative_zone", new CreativeZoneBlock());
        CREATIVE_ZONE_ITEM = RegisterBlockItem("creative_zone_item", new CreativeZoneItem(CREATIVE_ZONE, new FabricItemSettings().maxCount(1)));
        CREATIVE_ZONE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "creative_zone_entity"), FabricBlockEntityTypeBuilder.create(CreativeZoneBlockEntity::new, CREATIVE_ZONE).build());
        SOUL_JAR = RegisterBlock("soul_jar", new SoulJarBlock());
        SOUL_JAR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "soul_jar_block_entity"), FabricBlockEntityTypeBuilder.create(SoulJarBlockEntity::new, SOUL_JAR).build());

        ARNICITE_BLOCK = RegisterBlock("arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(ARNICITE_BLOCK_SOUNDS));
        CUT_ARNICITE_BLOCK = RegisterBlock("cut_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(ARNICITE_BLOCK_SOUNDS));
        ARNICITE_BLOCK_ITEM = RegisterBlockItem("arnicite_block", ARNICITE_BLOCK, new FabricItemSettings());
        CUT_ARNICITE_BLOCK_ITEM = RegisterBlockItem("cut_arnicite_block", CUT_ARNICITE_BLOCK, new FabricItemSettings());
        DETERMINATION_ARNICITE_BLOCK = RegisterBlock("determination_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(10).sounds(ARNICITE_BLOCK_SOUNDS));
        CUT_DETERMINATION_ARNICITE_BLOCK = RegisterBlock("cut_determination_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(10).sounds(ARNICITE_BLOCK_SOUNDS));
        DETERMINATION_ARNICITE_BLOCK_ITEM = RegisterBlockItem("determination_arnicite_block", DETERMINATION_ARNICITE_BLOCK, new FabricItemSettings());
        CUT_DETERMINATION_ARNICITE_BLOCK_ITEM = RegisterBlockItem("cut_determination_arnicite_block", CUT_DETERMINATION_ARNICITE_BLOCK, new FabricItemSettings());
        BRAVERY_ARNICITE_BLOCK = RegisterBlock("bravery_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(10).sounds(ARNICITE_BLOCK_SOUNDS));
        CUT_BRAVERY_ARNICITE_BLOCK = RegisterBlock("cut_bravery_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(10).sounds(ARNICITE_BLOCK_SOUNDS));
        BRAVERY_ARNICITE_BLOCK_ITEM = RegisterBlockItem("bravery_arnicite_block", BRAVERY_ARNICITE_BLOCK, new FabricItemSettings());
        CUT_BRAVERY_ARNICITE_BLOCK_ITEM = RegisterBlockItem("cut_bravery_arnicite_block", CUT_BRAVERY_ARNICITE_BLOCK, new FabricItemSettings());
        JUSTICE_ARNICITE_BLOCK = RegisterBlock("justice_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(10).sounds(ARNICITE_BLOCK_SOUNDS));
        CUT_JUSTICE_ARNICITE_BLOCK = RegisterBlock("cut_justice_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(10).sounds(ARNICITE_BLOCK_SOUNDS));
        JUSTICE_ARNICITE_BLOCK_ITEM = RegisterBlockItem("justice_arnicite_block", JUSTICE_ARNICITE_BLOCK, new FabricItemSettings());
        CUT_JUSTICE_ARNICITE_BLOCK_ITEM = RegisterBlockItem("cut_justice_arnicite_block", CUT_JUSTICE_ARNICITE_BLOCK, new FabricItemSettings());
        KINDNESS_ARNICITE_BLOCK = RegisterBlock("kindness_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(10).sounds(ARNICITE_BLOCK_SOUNDS));
        CUT_KINDNESS_ARNICITE_BLOCK = RegisterBlock("cut_kindness_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(10).sounds(ARNICITE_BLOCK_SOUNDS));
        KINDNESS_ARNICITE_BLOCK_ITEM = RegisterBlockItem("kindness_arnicite_block", KINDNESS_ARNICITE_BLOCK, new FabricItemSettings());
        CUT_KINDNESS_ARNICITE_BLOCK_ITEM = RegisterBlockItem("cut_kindness_arnicite_block", CUT_KINDNESS_ARNICITE_BLOCK, new FabricItemSettings());
        PATIENCE_ARNICITE_BLOCK = RegisterBlock("patience_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(10).sounds(ARNICITE_BLOCK_SOUNDS));
        CUT_PATIENCE_ARNICITE_BLOCK = RegisterBlock("cut_patience_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(10).sounds(ARNICITE_BLOCK_SOUNDS));
        PATIENCE_ARNICITE_BLOCK_ITEM = RegisterBlockItem("patience_arnicite_block", PATIENCE_ARNICITE_BLOCK, new FabricItemSettings());
        CUT_PATIENCE_ARNICITE_BLOCK_ITEM = RegisterBlockItem("cut_patience_arnicite_block", CUT_PATIENCE_ARNICITE_BLOCK, new FabricItemSettings());
        INTEGRITY_ARNICITE_BLOCK = RegisterBlock("integrity_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(10).sounds(ARNICITE_BLOCK_SOUNDS));
        CUT_INTEGRITY_ARNICITE_BLOCK = RegisterBlock("cut_integrity_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(10).sounds(ARNICITE_BLOCK_SOUNDS));
        INTEGRITY_ARNICITE_BLOCK_ITEM = RegisterBlockItem("integrity_arnicite_block", INTEGRITY_ARNICITE_BLOCK, new FabricItemSettings());
        CUT_INTEGRITY_ARNICITE_BLOCK_ITEM = RegisterBlockItem("cut_integrity_arnicite_block", CUT_INTEGRITY_ARNICITE_BLOCK, new FabricItemSettings());
        PERSEVERANCE_ARNICITE_BLOCK = RegisterBlock("perseverance_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(10).sounds(ARNICITE_BLOCK_SOUNDS));
        CUT_PERSEVERANCE_ARNICITE_BLOCK = RegisterBlock("cut_perseverance_arnicite_block", FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(10).sounds(ARNICITE_BLOCK_SOUNDS));
        PERSEVERANCE_ARNICITE_BLOCK_ITEM = RegisterBlockItem("perseverance_arnicite_block", PERSEVERANCE_ARNICITE_BLOCK, new FabricItemSettings());
        CUT_PERSEVERANCE_ARNICITE_BLOCK_ITEM = RegisterBlockItem("cut_perseverance_arnicite_block", CUT_PERSEVERANCE_ARNICITE_BLOCK, new FabricItemSettings());
    }
}
