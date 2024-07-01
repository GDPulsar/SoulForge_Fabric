package com.pulsar.soulforge.components;

import com.pulsar.soulforge.SoulForge;
import dev.onyxstudios.cca.api.v3.component.ComponentFactory;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public final class WorldInitializer implements WorldComponentInitializer {
    public static final ComponentKey<WorldConfigComponent> WORLD_CONFIG;

    static {
        WORLD_CONFIG = ComponentRegistry.getOrCreate(Identifier.of(SoulForge.MOD_ID, "world_config"), WorldConfigComponent.class);
    }

    public static void register() {}

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(WORLD_CONFIG, world -> new WorldComponent());
    }
}
