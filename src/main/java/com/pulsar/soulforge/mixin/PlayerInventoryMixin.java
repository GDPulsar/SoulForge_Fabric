package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.tag.SoulForgeTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow @Final public PlayerEntity player;

    @Shadow public int selectedSlot;

    @Shadow public abstract ItemStack getStack(int slot);

    @ModifyConstant(method="scrollInHotbar", constant = @Constant(intValue = 9))
    private int modifyScrollHotbarSize(int constant) {
        PlayerEntity player = this.player;
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        return playerSoul.hasWeapon() ? 10 : 9;
    }

    @ModifyConstant(method="isValidHotbarIndex", constant = @Constant(intValue = 9))
    private static int modifyValidHotbarSize(int constant) {
        return 10;
    }

    @ModifyConstant(method="getHotbarSize", constant = @Constant(intValue = 9))
    private static int modifyHotbarSize(int constant) {
        return 10;
    }

    @Inject(method = "getMainHandStack", at=@At("HEAD"), cancellable = true)
    public void getMainHandStack(CallbackInfoReturnable<ItemStack> cir) {
        if (this.selectedSlot == 9) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            cir.setReturnValue(playerSoul.getWeapon());
        }
    }

    @Inject(method = "swapSlotWithHotbar", at=@At("HEAD"), cancellable = true)
    public void modifyHotbarSwap(int slot, CallbackInfo ci) {
        if (this.selectedSlot == 9 || slot == 9) ci.cancel();
    }

    @Inject(method = "updateItems", at = @At("HEAD"))
    private void modifyUpdatingItems(CallbackInfo ci) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(this.player);
        playerSoul.getWeapon().inventoryTick(this.player.getWorld(), this.player, 0, this.selectedSlot == 9);
    }

    @WrapWithCondition(method = "damageArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"))
    private <T extends LivingEntity> boolean modifyDamageArmor(ItemStack instance, int amount, T entity, Consumer<T> breakCallback, @Local DamageSource damageSource) {
        return !damageSource.isIn(SoulForgeTags.NO_ARMOR_BREAK);
    }
}
