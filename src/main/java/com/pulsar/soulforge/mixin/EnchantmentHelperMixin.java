package com.pulsar.soulforge.mixin;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.siphon.Siphon;
import com.pulsar.soulforge.trait.Traits;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getLevel", at=@At("RETURN"), cancellable = true)
    private static void modifyEnchantmentLevel(Enchantment enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (enchantment == Enchantments.PIERCING) {
            if (stack.hasNbt() && stack.getNbt().contains("Siphon")) {
                Siphon.Type type = Siphon.Type.getSiphon(stack.getNbt().getString("Siphon"));
                if (type == Siphon.Type.PERSEVERANCE || type == Siphon.Type.SPITE) {
                    cir.setReturnValue(cir.getReturnValue() + 1);
                }
            }
        }
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
}
