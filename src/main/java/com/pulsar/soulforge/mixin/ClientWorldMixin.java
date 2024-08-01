package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.soulforge.accessors.HasTickManager;
import com.pulsar.soulforge.util.TickManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements HasTickManager {
    @Unique
    TickManager tickManager = new TickManager();

    @Override
    public TickManager getTickManager() {
        return tickManager;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;tickTime()V"))
    private void modifyTickTime(ClientWorld instance) {
        if (tickManager.shouldTick()) {
            instance.tickTime();
        }
    }

    @ModifyExpressionValue(method = "method_32124(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;hasVehicle()Z"))
    private boolean modifyTickEntities(boolean original, @Local Entity entity) {
        return original || tickManager.shouldSkipTick(entity);
    }
}
