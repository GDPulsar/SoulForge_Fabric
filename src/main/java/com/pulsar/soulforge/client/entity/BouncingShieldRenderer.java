package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.BouncingShieldEntity;
import com.pulsar.soulforge.entity.DeterminationSpearProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BouncingShieldRenderer extends GeoEntityRenderer<BouncingShieldEntity> {
    public BouncingShieldRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BouncingShieldModel());
    }
}
