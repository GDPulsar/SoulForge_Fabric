package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.DeterminationSpearProjectile;
import com.pulsar.soulforge.entity.SOJProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SOJRenderer extends GeoEntityRenderer<SOJProjectile> {
    public SOJRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SOJModel());
    }
}
