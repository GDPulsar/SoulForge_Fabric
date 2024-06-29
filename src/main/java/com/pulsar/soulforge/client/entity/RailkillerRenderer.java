package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.AntihealDartProjectile;
import com.pulsar.soulforge.entity.RailkillerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RailkillerRenderer extends GeoEntityRenderer<RailkillerEntity> {
    public RailkillerRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new RailkillerModel());
    }
}
