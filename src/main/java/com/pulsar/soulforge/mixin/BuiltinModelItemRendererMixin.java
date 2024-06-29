package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.client.entity.LightningRodRenderer;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 1))
    private boolean modifyIsOfTrident(ItemStack instance, Item item) {
        return instance.isOf(item) || instance.isOf(SoulForgeItems.LIGHTNING_ROD);
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/TridentEntityModel;getLayer(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier modifyTridentModelTexture(Identifier original, @Local ItemStack stack) {
        if (stack.isOf(SoulForgeItems.LIGHTNING_ROD)) {
            return LightningRodRenderer.TEXTURE;
        }
        return original;
    }
}
