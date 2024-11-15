package com.pulsar.soulforge.mixin;

import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpawnHelper.class)
public class SpawnHelperMixin {
    /*@ModifyReturnValue(method = "isAcceptableSpawnPosition", at = @At("RETURN"))
    private static boolean canSpawnAt(boolean original, @Local ServerWorld world, @Local BlockPos.Mutable pos) {
        if (SoulForge.getWorldComponent(world).isInRangeOfActiveCreativeZone(pos)) return false;
        return original;
    }*/
}
