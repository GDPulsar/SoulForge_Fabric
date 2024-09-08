package com.pulsar.soulforge.registries;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.trait.DataDrivenTrait;
import com.pulsar.soulforge.trait.DataDrivenTraitSerializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;

public class TraitReloadListener extends JsonDataLoader implements IdentifiableResourceReloadListener {
    public static final String ID = "soulforge/traits";
    public static final TraitReloadListener INSTANCE = new TraitReloadListener();

    private TraitReloadListener() {
        super(new Gson(), ID);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(SoulForge.MOD_ID, ID);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        //Traits.deloadCustomTraits();
        prepared.forEach(((identifier, jsonElement) -> {
            JsonObject json = jsonElement.getAsJsonObject();
            try {
                DataDrivenTrait trait = DataDrivenTraitSerializer.fromJson(json);
                SoulForge.LOGGER.info("Added trait: {}", trait.getId());
                //Traits.addCustomTrait(trait);
            } catch (Exception e) {
                SoulForge.LOGGER.error("An error occurred while loading ability {}. Error: {}", identifier, e.getLocalizedMessage());
            }
        }));
        SoulForge.LOGGER.info("Finished loading custom traits.");
    }
}
