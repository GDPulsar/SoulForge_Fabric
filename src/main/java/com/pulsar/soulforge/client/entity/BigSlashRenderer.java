package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.BigSlashProjectile;
import com.pulsar.soulforge.entity.SmallSlashProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BigSlashRenderer extends GeoEntityRenderer<BigSlashProjectile> {
    public BigSlashRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BigSlashModel());
    }
}
