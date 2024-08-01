package com.pulsar.soulforge.mixin;

import com.mojang.datafixers.DataFixer;
import com.pulsar.soulforge.accessors.HasTickManager;
import com.pulsar.soulforge.util.ServerTickManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.ApiServices;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements HasTickManager {
    @Unique
    ServerTickManager tickManager = new ServerTickManager((MinecraftServer)(Object)this);

    @Override
    public ServerTickManager getTickManager() {
        return tickManager;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void createTickManager(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        tickManager = new ServerTickManager((MinecraftServer)(Object)this);
    }
}
