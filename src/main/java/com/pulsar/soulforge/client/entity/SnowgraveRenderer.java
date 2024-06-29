package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.SnowgraveProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SnowgraveRenderer extends GeoEntityRenderer<SnowgraveProjectile> {
    public SnowgraveRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SnowgraveModel());
    }
}
