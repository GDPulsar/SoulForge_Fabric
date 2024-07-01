package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.SoulForge;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpawnHelper.class)
public class SpawnHelperMixin {
    @ModifyReturnValue(method = "isAcceptableSpawnPosition", at = @At("RETURN"))
    private static boolean canSpawnAt(boolean original, @Local ServerWorld world, @Local BlockPos.Mutable pos) {
        if (SoulForge.getWorldComponent(world).isInRangeOfActiveCreativeZone(pos)) return false;
        return original;
    }
}
