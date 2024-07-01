package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.recipe.SiphonRecipe;
import com.pulsar.soulforge.recipe.SoulForgeRecipes;
import com.pulsar.soulforge.tag.SoulForgeTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {
    @Shadow @Final private World world;

    @Shadow @Final private List<SmithingRecipe> recipes;

    public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "getForgingSlotsManager", at=@At("HEAD"), cancellable = true)
    protected void modifySlotManager(CallbackInfoReturnable<ForgingSlotsManager> cir) {
        cir.setReturnValue(ForgingSlotsManager.create()
                .input(0, 8, 48, stack ->
                        this.recipes.stream().anyMatch(recipe ->
                                recipe.testTemplate(stack)
                        ) || stack.isOf(SoulForgeItems.SIPHON_TEMPLATE)
                )
                .input(1, 26, 48, stack ->
                        this.recipes.stream().anyMatch(recipe ->
                                recipe.testBase(stack)
                        ) || stack.isIn(SoulForgeTags.SIPHONABLE)
                )
                .input(2, 44, 48, stack ->
                        this.recipes.stream().anyMatch(recipe ->
                                recipe.testAddition(stack)
                        ) || stack.isIn(SoulForgeTags.SIPHON_ADDITION)
                ).output(3, 98, 48).build());
    }

    @Inject(method = "canTakeOutput", at=@At("HEAD"), cancellable = true)
    protected void canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        List<SiphonRecipe> list = this.world.getRecipeManager().getAllMatches(SoulForgeRecipes.SIPHON_RECIPE, this.input, this.world);
        if (!list.isEmpty()) {
            SiphonRecipe recipeEntry = list.get(0);
            ItemStack itemStack = recipeEntry.craft(this.input, this.world.getRegistryManager());
            if (itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "updateResult", at = @At("TAIL"))
    protected void updateResult(CallbackInfo ci) {
        List<SiphonRecipe> list = this.world.getRecipeManager().getAllMatches(SoulForgeRecipes.SIPHON_RECIPE, this.input, this.world);
        if (!list.isEmpty()) {
            SiphonRecipe recipeEntry = list.get(0);
            ItemStack itemStack = recipeEntry.craft(this.input, this.world.getRegistryManager());
            if (itemStack.isItemEnabled(this.world.getEnabledFeatures())) {
                this.output.setLastRecipe(recipeEntry);
                this.output.setStack(0, itemStack);
            }
        }
    }

    @Inject(method = "getQuickMoveSlot", at=@At("HEAD"), cancellable = true)
    private static void getQuickMoveSlot(SmithingRecipe recipe, ItemStack stack, CallbackInfoReturnable<Optional<Integer>> cir) {
        if (stack.isOf(SoulForgeItems.SIPHON_TEMPLATE)) cir.setReturnValue(Optional.of(0));
        if (stack.isIn(SoulForgeTags.SIPHONABLE)) cir.setReturnValue(Optional.of(1));
        if (stack.isIn(SoulForgeTags.SIPHON_ADDITION)) cir.setReturnValue(Optional.of(2));
    }
}
