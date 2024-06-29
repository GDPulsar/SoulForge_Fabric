package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.FireTornadoProjectile;
import com.pulsar.soulforge.entity.SnowgraveProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireTornadoRenderer extends GeoEntityRenderer<FireTornadoProjectile> {
    public FireTornadoRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new FireTornadoModel());
    }
}
