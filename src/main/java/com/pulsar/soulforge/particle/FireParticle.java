package com.pulsar.soulforge.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class FireParticle extends SpriteBillboardParticle {
    protected FireParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteSet, double vx, double vy, double vz) {
        super(world, x, y, z, vx, vy, vz);

        this.velocityMultiplier = 1f;
        this.scale = 0.2f;
        this.maxAge = 35;
        this.setSpriteForAge(spriteSet);

        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;
        this.alpha = 1f;
    }

    @Override
    public void tick() {
        this.velocityX += (Math.random() - 0.5f) * 0.003f;
        this.velocityY += (Math.random() - 0.5f) * 0.003f;
        this.velocityZ += (Math.random() - 0.5f) * 0.003f;
        this.velocityMultiplier *= 0.995f;
        float color = MathHelper.clampedLerp(1f, 0f, (this.age - 10f) / 15f);
        this.red = color;
        this.green = color;
        this.blue = color;
        this.alpha = MathHelper.clampedLerp(1f, 0f, (this.age - 20f) / 15f);
        if (this.age >= 25) this.scale = MathHelper.clampedLerp(0.2f, 0.05f, (this.age - 15f) / 25f);
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
            return new FireParticle(world, x, y, z, this.sprites, vx, vy, vz);
        }
    }
}
