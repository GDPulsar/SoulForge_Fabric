package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.item.devices.machines.SiphonImbuer;
import com.pulsar.soulforge.item.special.CrushingRound;
import com.pulsar.soulforge.item.special.FrostbiteRound;
import com.pulsar.soulforge.item.special.PuncturingRound;
import com.pulsar.soulforge.item.special.SuppressingRound;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {
    @Inject(method = "getPullTime", at=@At("RETURN"), cancellable = true)
    private static void modifyPullTime(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        if (stack.get(SoulForgeItems.IMBUED_COMPONENT)) {
            cir.setReturnValue(cir.getReturnValue()/5);
        }
    }

    @Inject(method = "loadProjectiles", at=@At("HEAD"), cancellable = true)
    private static void onLoadProjectile(LivingEntity shooter, ItemStack crossbow, CallbackInfoReturnable<Boolean> cir) {
        if (shooter instanceof PlayerEntity player) {
            if (crossbow.get(SoulForgeItems.IMBUED_COMPONENT)) {
                ItemStack stack = Utils.getImbuer(crossbow, player);
                if (stack != null) {
                    if (((SiphonImbuer)stack.getItem()).getCharge(stack) <= 0) cir.setReturnValue(false);
                    ((SiphonImbuer) stack.getItem()).decreaseCharge(stack, 3);
                }
            }
        }
    }
}
