package com.pulsar.soulforge.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;

public class PelletTrailParticle extends SpriteBillboardParticle {
    protected PelletTrailParticle(ClientWorld world, SpriteProvider spriteSet, double x, double y, double z) {
        super(world, x, y, z);

        this.velocityMultiplier = 0f;
        this.scale = 0.135f;
        this.maxAge = 3;
        this.red = 1f;
        this.green = 1f;
        this.blue = 0f;
        this.setSprite(spriteSet.getSprite(this.random));
    }

    @Override
    public void tick() {
        this.scale -= 0.045f;
        super.tick();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double vx, double vy, double vz) {
            return new PelletTrailParticle(world, this.sprites, x, y, z);
        }
    }
}
