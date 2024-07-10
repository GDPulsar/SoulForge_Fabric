package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.JusticeArrowProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class JusticeArrowRenderer extends ProjectileEntityRenderer<JusticeArrowProjectile> {
    public static final Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/projectiles/justice_arrow.png");

    public JusticeArrowRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public Identifier getTexture(JusticeArrowProjectile justiceArrow) { return TEXTURE; }
}
