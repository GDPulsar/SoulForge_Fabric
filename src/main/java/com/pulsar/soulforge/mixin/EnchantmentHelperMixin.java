package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.item.SoulForgeItems;
import com.pulsar.soulforge.siphon.Siphon;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyReturnValue(method = "getAmmoUse", at = @At("RETURN"))
    private static int modifyAmmoUse(int original, @Local ItemStack rangedWeaponStack) {
        if (rangedWeaponStack.get(SoulForgeItems.SIPHON_COMPONENT) != null) {
            Siphon.Type type = Siphon.Type.getSiphon(rangedWeaponStack.get(SoulForgeItems.SIPHON_COMPONENT));
            if (type == Siphon.Type.JUSTICE) {
                return 0;
            }
        }
        return original;
    }

    @ModifyReturnValue(method = "getProjectilePiercing", at = @At("RETURN"))
    private static int modifyPiercingLevel(int original, @Local ItemStack weaponStack) {
        if (weaponStack.get(SoulForgeItems.SIPHON_COMPONENT) != null) {
            Siphon.Type type = Siphon.Type.getSiphon(weaponStack.get(SoulForgeItems.SIPHON_COMPONENT));
            if (type == Siphon.Type.PERSEVERANCE) {
                return original + 1;
            }
        }
        return original;
    }
}
