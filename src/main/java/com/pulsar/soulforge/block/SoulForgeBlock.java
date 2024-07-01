package com.pulsar.soulforge.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SoulForgeBlock extends BlockWithEntity implements BlockEntityProvider {
    public SoulForgeBlock() {
        super(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque().notSolid().pistonBehavior(PistonBehavior.IGNORE).requiresTool());
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SoulForgeBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SoulForgeBlockEntity) {
                ItemScatterer.spawn(world, pos, (SoulForgeBlockEntity)blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            if (player.getMainHandStack().isOf(Items.LAVA_BUCKET)) {
                SoulForgeBlockEntity blockEntity = (SoulForgeBlockEntity)world.getBlockEntity(pos);
                if (blockEntity.canInsertLava()) {
                    ItemStack empty = new ItemStack(Items.BUCKET);
                    if (player.getMainHandStack().getCount() > 1) {
                        if (player.giveItemStack(empty)) {
                            player.getMainHandStack().decrement(1);
                            blockEntity.addLava();
                            player.playSound(SoundEvents.ITEM_BUCKET_EMPTY, 1f, 1f);
                        }
                    } else {
                        player.setStackInHand(Hand.MAIN_HAND, empty);
                        blockEntity.addLava();
                        player.playSound(SoundEvents.ITEM_BUCKET_EMPTY, 1f, 1f);
                    }
                }
            } else {
                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
                if (screenHandlerFactory != null) {
                    player.openHandledScreen(screenHandlerFactory);
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, SoulForgeBlocks.SOUL_FORGE_BLOCK_ENTITY,
                ((world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1)));
    }
}
