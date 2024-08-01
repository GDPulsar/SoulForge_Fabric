package com.pulsar.soulforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.pulsar.soulforge.accessors.HasTickManager;
import com.pulsar.soulforge.util.TickManager;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.raid.RaidManager;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements HasTickManager {
    @Shadow protected abstract boolean shouldCancelSpawn(Entity entity);

    @Shadow @Final private MinecraftServer server;

    @Override
    public TickManager getTickManager() {
        return ((HasTickManager)this.server).getTickManager();
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/border/WorldBorder;tick()V"))
    private boolean canTickWorldBorder(WorldBorder instance) {
        return getTickManager().shouldTick();
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickWeather()V"))
    private boolean canTickWeather(ServerWorld instance) {
        return getTickManager().shouldTick();
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickTime()V"))
    private boolean canTickTime(ServerWorld instance) {
        return getTickManager().shouldTick();
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;isDebugWorld()Z"))
    private boolean canTickBlocksAndFluids(boolean original) {
        return original || !getTickManager().shouldTick();
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/raid/RaidManager;tick()V"))
    private boolean canTickRaids(RaidManager instance) {
        return getTickManager().shouldTick();
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;processSyncedBlockEvents()V"))
    private boolean canTickBlockEvents(ServerWorld instance) {
        return getTickManager().shouldTick();
    }

    @Inject(method = "method_31420(Lnet/minecraft/util/profiler/Profiler;Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void modifyShouldCancelSpawn(Profiler profiler, Entity entity, CallbackInfo ci) {
        if (getTickManager().shouldSkipTick(entity)) ci.cancel();
    }
}
