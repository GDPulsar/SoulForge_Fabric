package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.BraverySpearProjectile;
import com.pulsar.soulforge.entity.IncendiaryGrenadeEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IncendiaryGrenadeRenderer extends GeoEntityRenderer<IncendiaryGrenadeEntity> {
    public IncendiaryGrenadeRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new IncendiaryGrenadeModel());
    }
}
