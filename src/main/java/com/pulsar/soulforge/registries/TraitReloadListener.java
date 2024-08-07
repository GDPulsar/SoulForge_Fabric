package com.pulsar.soulforge.registries;

import com.pulsar.soulforge.SoulForge;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TraitReloadListener implements IdentifiableResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return new Identifier(SoulForge.MOD_ID, "traits");
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {

        for (Map.Entry<Identifier, Resource> traitResource : manager.findResources("traits", path -> path.getPath().endsWith(".json")).entrySet()) {
            try (InputStream stream = traitResource.getValue().getInputStream()) {

            } catch (Exception e) {
                SoulForge.LOGGER.error("An error occurred while loading trait {}. Error: {}", traitResource.getKey().toString(), e);
            }
        }

        return null;
    }
}
