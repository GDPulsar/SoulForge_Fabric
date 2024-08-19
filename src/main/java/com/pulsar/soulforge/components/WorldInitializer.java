package com.pulsar.soulforge.components;

import com.pulsar.soulforge.SoulForge;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.util.Identifier;

public final class WorldInitializer implements WorldComponentInitializer {
    public static final ComponentKey<WorldComponent> WORLD_CONFIG;

    static {
        WORLD_CONFIG = ComponentRegistry.getOrCreate(new Identifier(SoulForge.MOD_ID, "world_config"), WorldComponent.class);
    }

    public static void register() {}

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(WORLD_CONFIG, world -> new WorldComponent());
    }
}
