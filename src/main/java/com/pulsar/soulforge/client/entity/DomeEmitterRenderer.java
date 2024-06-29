package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.BraverySpearProjectile;
import com.pulsar.soulforge.entity.DomeEmitterEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DomeEmitterRenderer extends GeoEntityRenderer<DomeEmitterEntity> {
    public DomeEmitterRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DomeEmitterModel());
    }
}
