package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.DeterminationSpearProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DeterminationSpearRenderer extends GeoEntityRenderer<DeterminationSpearProjectile> {
    public DeterminationSpearRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DeterminationSpearModel());
    }
}
