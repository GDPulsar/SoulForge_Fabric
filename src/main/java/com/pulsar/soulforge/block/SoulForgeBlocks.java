package com.pulsar.soulforge.block;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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

    public static Block SOUL_FORGE_BLOCK;
    public static Item SOUL_FORGE_ITEM;
    public static BlockEntityType<SoulForgeBlockEntity> SOUL_FORGE_BLOCK_ENTITY;
    public static Block DOME_BLOCK;
    public static Block DETERMINATION_DOME_BLOCK;
    public static Block CREATIVE_ZONE;
    public static Item CREATIVE_ZONE_ITEM;
    public static BlockEntityType<CreativeZoneBlockEntity> CREATIVE_ZONE_ENTITY;

    public static void registerBlocks() {
        SOUL_FORGE_BLOCK = RegisterBlock("soul_forge_block", new SoulForgeBlock());
        SOUL_FORGE_ITEM = RegisterBlockItem("soul_forge_item", SOUL_FORGE_BLOCK, new FabricItemSettings());
        SOUL_FORGE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "soul_forge_block_entity"), FabricBlockEntityTypeBuilder.create(SoulForgeBlockEntity::new, SOUL_FORGE_BLOCK).build());
        DOME_BLOCK = RegisterBlock("dome_block", new DomeBlock());
        DETERMINATION_DOME_BLOCK = RegisterBlock("determination_dome_block", new DeterminationDomeBlock());
        CREATIVE_ZONE = RegisterBlock("creative_zone", new CreativeZoneBlock());
        CREATIVE_ZONE_ITEM = RegisterBlockItem("creative_zone_item", new CreativeZoneItem(CREATIVE_ZONE, new FabricItemSettings().maxCount(1)));
        CREATIVE_ZONE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SoulForge.MOD_ID, "creative_zone_entity"), FabricBlockEntityTypeBuilder.create(CreativeZoneBlockEntity::new, CREATIVE_ZONE).build());
    }
}
