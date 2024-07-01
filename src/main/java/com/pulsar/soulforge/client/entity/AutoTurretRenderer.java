package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.AutoTurretEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AutoTurretRenderer extends GeoEntityRenderer<AutoTurretEntity> {
    public AutoTurretRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AutoTurretModel());
    }
}
