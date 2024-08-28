package com.pulsar.soulforge.components;

import com.pulsar.soulforge.SoulForge;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public final class EntityInitializer implements EntityComponentInitializer {
    public static final ComponentKey<SoulComponent> SOUL;
    public static final ComponentKey<TemporaryModifierComponent> TEMPORARY_MODIFIERS;
    public static final ComponentKey<ValueComponent> VALUES;
    public static final ComponentKey<HateComponent> HATE;

    public static boolean hasRegistered = false;

    static {
        SOUL = ComponentRegistry.getOrCreate(new Identifier(SoulForge.MOD_ID, "trait"), SoulComponent.class);
        TEMPORARY_MODIFIERS = ComponentRegistry.getOrCreate(new Identifier(SoulForge.MOD_ID, "temporary_modifiers"), TemporaryModifierComponent.class);
        VALUES = ComponentRegistry.getOrCreate(new Identifier(SoulForge.MOD_ID, "values"), ValueComponent.class);
        HATE = ComponentRegistry.getOrCreate(new Identifier(SoulForge.MOD_ID, "hate"), HateComponent.class);
    }

    public static void register() {}

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(LivingEntity.class, VALUES)
                .end(ValueComponent::new);
        registry.beginRegistration(LivingEntity.class, HATE)
                .end(HateComponent::new);
        registry.beginRegistration(LivingEntity.class, TEMPORARY_MODIFIERS)
                .end(TemporaryModifierComponent::new);
        registry.beginRegistration(PlayerEntity.class, SOUL)
                .respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY)
                .end(SoulComponent::new);
        hasRegistered = true;
    }
}
