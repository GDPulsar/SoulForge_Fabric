package com.pulsar.soulforge.mixin;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    /*@Shadow protected abstract boolean shouldCancelSpawn(Entity entity);

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
    }*/
}
