package com.pulsar.soulforge.registries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.trait.DataDrivenTrait;
import com.pulsar.soulforge.trait.Traits;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.BufferedReader;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SoulForgeReloadListener implements IdentifiableResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return new Identifier(SoulForge.MOD_ID, "soulforge");
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {

        for (Map.Entry<Identifier, Resource> traitResource : manager.findResources("soulforge/traits", path -> path.getPath().endsWith(".json")).entrySet()) {
            try (BufferedReader reader = traitResource.getValue().getReader()) {
                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(DataDrivenTrait.class, new DataDrivenTrait.DataDrivenTraitSerializer());
                Gson gson = builder.create();
                DataDrivenTrait trait = gson.fromJson(reader, DataDrivenTrait.class);
                SoulForge.LOGGER.info("Added Datapack Trait: {}", trait.getName());
                Traits.addCustomTrait(trait);
            } catch (Exception e) {
                SoulForge.LOGGER.error("An error occurred while loading trait {}. Error: {}", traitResource.getKey().toString(), e);
            }
        }

        return CompletableFuture.completedFuture(null);
    }
}
