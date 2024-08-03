package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.weapons.BraverySpear;
import com.pulsar.soulforge.item.weapons.PerseveranceEdge;
import com.pulsar.soulforge.item.weapons.weapon_wheel.*;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyReturnValue(method = "getLevel", at=@At("RETURN"))
    private static int soulforge$modifyEnchantmentLevel(int original, @Local Enchantment enchantment, @Local ItemStack stack) {
        if (enchantment == Enchantments.PIERCING) {
            if (stack.hasNbt() && stack.getNbt().contains("Siphon")) {
                Siphon.Type type = Siphon.Type.getSiphon(stack.getNbt().getString("Siphon"));
                if (type == Siphon.Type.PERSEVERANCE || type == Siphon.Type.SPITE) {
                    return original + 1;
                }
            }
        }
        return original;
    }

    @Inject(method="getFireAspect", at=@At("HEAD"), cancellable = true)
    private static void modifyFireAspect(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (entity instanceof PlayerEntity player) {
            SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
            if ((playerSoul.hasCast("Bravery Boost") || playerSoul.hasCast("Perfected Aura Technique") ||
                    playerSoul.hasCast("Fearless Instincts"))
                    && !(playerSoul.getTraits().contains(Traits.bravery) && playerSoul.getTraits().contains(Traits.patience))) {
                cir.setReturnValue(2);
            }
        }
    }

    @Inject(method = "getSweepingMultiplier", at = @At("HEAD"), cancellable = true)
    private static void modifySweepingMultiplier(LivingEntity entity, CallbackInfoReturnable<Float> cir) {
        Item item = entity.getMainHandStack().getItem();
        if (item instanceof BraverySpear || item instanceof DeterminationSpear) cir.setReturnValue(0.33f);
        if (item instanceof DeterminationGreatsword) cir.setReturnValue(0.66f);
        if (item instanceof PerseveranceEdge || item instanceof DeterminationEdge) cir.setReturnValue(1f);
        if (item instanceof RealKnife) cir.setReturnValue(0.33f);
        if (item instanceof DeterminationSword) cir.setReturnValue(0.33f);
    }
}
