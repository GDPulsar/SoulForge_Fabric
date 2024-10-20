package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.SkullProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkullProjectileRenderer extends GeoEntityRenderer<SkullProjectile> {
    public SkullProjectileRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SkullProjectileModel());
    }
}
