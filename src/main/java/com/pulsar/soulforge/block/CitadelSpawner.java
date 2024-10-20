package com.pulsar.soulforge.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.sound.BlockSoundGroup;

public class CitadelSpawner extends SpawnerBlock {
    public CitadelSpawner() {
        super(FabricBlockSettings.create().strength(50f, 50f).sounds(BlockSoundGroup.DEEPSLATE).pistonBehavior(PistonBehavior.BLOCK));
    }
}
