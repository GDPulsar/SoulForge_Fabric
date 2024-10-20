package com.pulsar.soulforge.block;

import com.pulsar.soulforge.sounds.SoulForgeSounds;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class KasoBlock extends Block {
    public KasoBlock() {
        super(FabricBlockSettings.copyOf(Blocks.BEDROCK).ticksRandomly());
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.playSound(null, pos, SoulForgeSounds.KASO_EVENT, SoundCategory.MASTER, 1f, 1f);
    }
}
