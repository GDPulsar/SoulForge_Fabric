package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.pulsar.soulforge.siphon.Siphon;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(ServerPlayerInteractionManager.class)
abstract class ServerPlayerInteractionManagerMixin {
    @Shadow @Final protected ServerPlayerEntity player;

    @ModifyReceiver(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"))
    private ItemStack soulforge$modifyBreakingTool(ItemStack stack) {
        ItemStack tool = stack.copy();
        if (tool.getNbt() != null) {
            if (tool.getNbt().contains("Siphon")) {
                Siphon.Type type = Siphon.Type.getSiphon(tool.getNbt().getString("Siphon"));
                if (type == Siphon.Type.PATIENCE || type == Siphon.Type.SPITE) {
                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.fromNbt(tool.getEnchantments());
                    int fortuneLevel = 0;
                    if (enchantments.containsKey(Enchantments.FORTUNE)) {
                        fortuneLevel = enchantments.get(Enchantments.FORTUNE);
                    }
                    enchantments.put(Enchantments.FORTUNE, fortuneLevel + 2);
                    EnchantmentHelper.set(enchantments, tool);
                }
            }
        }
        return tool;
    }
}
