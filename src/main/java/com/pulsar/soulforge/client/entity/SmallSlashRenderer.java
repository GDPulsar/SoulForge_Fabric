package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.entity.AutoTurretEntity;
import com.pulsar.soulforge.entity.SmallSlashProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SmallSlashRenderer extends GeoEntityRenderer<SmallSlashProjectile> {
    public SmallSlashRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SmallSlashModel());
    }
}
