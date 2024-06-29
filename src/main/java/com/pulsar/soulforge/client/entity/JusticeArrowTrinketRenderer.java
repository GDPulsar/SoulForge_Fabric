package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.JusticeArrowTrinketProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class JusticeArrowTrinketRenderer extends ProjectileEntityRenderer<JusticeArrowTrinketProjectile> {
    public JusticeArrowTrinketRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public static final Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/projectiles/justice_arrow.png");

    public Identifier getTexture(JusticeArrowTrinketProjectile arrowEntity) {
        return TEXTURE;
    }
}
