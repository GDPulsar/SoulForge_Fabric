package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;

public class JusticeArrowRenderer extends ProjectileEntityRenderer<ArrowEntity> {
    public static final Identifier TEXTURE = Identifier.of(SoulForge.MOD_ID, "textures/projectiles/justice_arrow.png");

    public JusticeArrowRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ArrowEntity arrowEntity) {
        return TEXTURE;
    }
}
