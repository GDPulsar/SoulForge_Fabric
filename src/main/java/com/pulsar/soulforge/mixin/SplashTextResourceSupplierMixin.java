package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {
    @ModifyReturnValue(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/List;", at=@At("RETURN"))
    private List<String> addSplashTexts(List<String> original) {
        original.add("Flaming always wins.");
        original.add("75% trans!");
        original.add("Let him cook.");
        original.add("chat, what's your favourite bird?");
        original.add("*insert horny quote here*");
        original.add("Apologize to PV Sagan!!!");
        original.add("glacid glacid");
        original.add("betty (fake)");
        original.add("betty (real)");
        original.add("ocean (uhhh?)");
        original.add("kaso moment");
        original.add("LETS GO GAMBLING!!!!");
        return original;
    }
}
