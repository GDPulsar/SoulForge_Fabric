package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.AntihealDartProjectile;
import com.pulsar.soulforge.entity.BraverySpearProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AntihealDartRenderer extends GeoEntityRenderer<AntihealDartProjectile> {
    public AntihealDartRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AntihealDartModel());
    }
}
