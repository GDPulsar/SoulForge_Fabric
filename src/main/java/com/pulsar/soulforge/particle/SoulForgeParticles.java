package com.pulsar.soulforge.particle;

import com.pulsar.soulforge.SoulForge;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SoulForgeParticles {
    public static final DefaultParticleType FIRE_PARTICLE = FabricParticleTypes.simple();

    public static void serverRegister() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(SoulForge.MOD_ID, "fire"), FIRE_PARTICLE);
    }

    public static void clientRegister() {
        ParticleFactoryRegistry.getInstance().register(FIRE_PARTICLE, FireParticle.Factory::new);
    }
}
