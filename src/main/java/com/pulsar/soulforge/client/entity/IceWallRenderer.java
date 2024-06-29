package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.SnowgraveProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceWallRenderer extends GeoEntityRenderer<SnowgraveProjectile> {
    public IceWallRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new IceWallModel());
    }
}
