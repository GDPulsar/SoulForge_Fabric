package com.pulsar.soulforge.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class FireParticle extends SpriteBillboardParticle {
    protected FireParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteSet, double dx, double dy, double dz) {
        super(world, x, y, z, dx, dy, dz);

        this.velocityMultiplier = 1f;
        this.x = dx;
        this.y = dy;
        this.z = dz;
        this.scale = 0.5f;
        this.maxAge = 50;
        this.setSpriteForAge(spriteSet);
        this.velocityX = 0f;
        this.velocityY = 0f;
        this.velocityZ = 0f;

        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;
        this.alpha = 1f;
    }

    @Override
    public void tick() {
        this.velocityY += 0.002f;
        this.velocityX += (Math.random() - 0.5f) * 0.003f;
        this.velocityZ += (Math.random() - 0.5f) * 0.003f;
        float color = MathHelper.clampedLerp(1f, 0f, (this.age - 20f) / 30f);
        this.red = color;
        this.green = color;
        this.blue = color;
        this.alpha = MathHelper.clampedLerp(1f, 0f, (this.age - 40f) / 30f);
        if (this.age >= 25) this.scale = MathHelper.clampedLerp(0.5f, 0.2f, (this.age - 25f) / 45f);
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
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {
            return new FireParticle(world, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
