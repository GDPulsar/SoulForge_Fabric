package com.pulsar.soulforge.components;

import com.pulsar.soulforge.SoulForge;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public final class EntityInitializer implements EntityComponentInitializer {
    public static final ComponentKey<SoulComponent> SOUL;

    static {
        SOUL = ComponentRegistry.getOrCreate(Identifier.of(SoulForge.MOD_ID, "trait"), SoulComponent.class);
    }

    public static void register() {}

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(PlayerEntity.class, SOUL)
                .respawnStrategy(RespawnCopyStrategy.CHARACTER)
                .end(PlayerSoulComponent::new);
    }
}
