package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.BraverySpearProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BraverySpearRenderer extends GeoEntityRenderer<BraverySpearProjectile> {
    public BraverySpearRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BraverySpearModel());
    }
}
