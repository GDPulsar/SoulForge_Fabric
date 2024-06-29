package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.BigSlashProjectile;
import com.pulsar.soulforge.entity.SwordSlashProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SwordSlashRenderer extends GeoEntityRenderer<SwordSlashProjectile> {
    public SwordSlashRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SwordSlashModel());
    }
}
