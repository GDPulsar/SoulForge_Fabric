package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.siphon.Siphon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(Block.class)
public class BlockMixin {
    @ModifyReturnValue(
            method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;",
            at = @At("RETURN"))
    private static List<ItemStack> getDroppedStacks(List<ItemStack> original, @Local ItemStack stack, @Local ServerWorld world) {
        List<ItemStack> items = new ArrayList<>();
        if (stack.getNbt() != null) {
            if (stack.getNbt().contains("Siphon")) {
                Siphon.Type type = Siphon.Type.getSiphon(stack.getNbt().getString("Siphon"));
                if (type == Siphon.Type.BRAVERY || type == Siphon.Type.SPITE) {
                    for (ItemStack itemStack : original) {
                        Optional<SmeltingRecipe> recipe = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(itemStack), world);

                        if (recipe.isPresent()) {
                            ItemStack smelted = recipe.get().getOutput(null).copy();
                            smelted.setCount(itemStack.getCount());
                            items.add(smelted);
                        } else {
                            items.add(itemStack);
                        }
                    }
                    return items;
                }
            }
        }
        return original;
    }

    @Inject(method = "afterBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void soulforge$givePlayerDroppedStacks(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (tool.getNbt() != null) {
            if (tool.getNbt().contains("Siphon")) {
                Siphon.Type type = Siphon.Type.getSiphon(tool.getNbt().getString("Siphon"));
                if (type == Siphon.Type.INTEGRITY || type == Siphon.Type.SPITE) {
                    for (ItemStack toDrop : Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, player, tool)) {
                        if (!player.giveItemStack(toDrop)) {
                            Block.dropStack(world, pos, toDrop);
                        }
                    }
                    ci.cancel();
                }
            }
        }
    }
}
