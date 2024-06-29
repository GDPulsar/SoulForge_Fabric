package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.siphon.Siphon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(ServerPlayerInteractionManager.class)
abstract class ServerPlayerInteractionManagerMixin {
    @Shadow @Final protected ServerPlayerEntity player;

    @Redirect(method = "tryBreakBlock", at=@At(value = "INVOKE", target = "Lnet/minecraft/block/Block;afterBreak(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/item/ItemStack;)V"))
    private void modifyBreakBlock(Block instance, World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool) {
        if (tool.getNbt() != null) {
            if (tool.getNbt().contains("Siphon")) {
                Siphon.Type type = Siphon.Type.getSiphon(tool.getNbt().getString("Siphon"));
                if (type == Siphon.Type.PATIENCE || type == Siphon.Type.SPITE) {
                    ItemStack fortuned = tool.copy();
                    int fortuneLevel = 0;
                    for (Map.Entry<Enchantment, Integer> enchantments : EnchantmentHelper.fromNbt(tool.getEnchantments()).entrySet()) {
                        if (enchantments.getKey() == Enchantments.FORTUNE) {
                            fortuneLevel = enchantments.getValue();
                            break;
                        }
                    }
                    fortuned.addEnchantment(Enchantments.FORTUNE, fortuneLevel+2);
                    instance.afterBreak(world, player, pos, state, blockEntity, fortuned);
                    return;
                }
                if (type == Siphon.Type.INTEGRITY || type == Siphon.Type.SPITE) {
                    player.incrementStat(Stats.MINED.getOrCreateStat(instance));
                    player.addExhaustion(0.005f);
                    for (ItemStack toDrop : Block.getDroppedStacks(state, (ServerWorld)world, pos, blockEntity, player, tool)) {
                        if (!player.giveItemStack(toDrop)) {
                            Block.dropStack(world, pos, toDrop);
                        }
                    }
                    return;
                }
            }
        }
        instance.afterBreak(world, player, pos, state, blockEntity, tool);
    }
}
