package com.pulsar.soulforge.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class FireParticle extends AbstractSlowingParticle {
    protected FireParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteset, double xd, double yd, double zd) {
        super(world, x, y, z, xd, yd, zd);
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {

    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }

    @Override
    public float getSize(float tickDelta) {
        float f = ((float)this.age + tickDelta) / (float)this.maxAge;
        return this.scale * (1.0f - f * f * 0.5f);
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;
        public Factory(SpriteProvider sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(DefaultParticleType particleType, ClientWorld world, double x, double y, double z, double xd, double yd, double zd) {
            return new FireParticle(world, x, y, z, this.sprites, xd, yd, zd);
        }
    }
}
