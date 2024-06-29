package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.BraverySpearProjectile;
import com.pulsar.soulforge.entity.ShieldShardEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShieldShardRenderer extends GeoEntityRenderer<ShieldShardEntity> {
    public ShieldShardRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ShieldShardModel());
    }
}
