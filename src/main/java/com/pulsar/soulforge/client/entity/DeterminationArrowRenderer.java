package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.DeterminationArrowProjectile;
import com.pulsar.soulforge.entity.JusticeArrowProjectile;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class DeterminationArrowRenderer extends ProjectileEntityRenderer<DeterminationArrowProjectile> {
    public static final Identifier TEXTURE = new Identifier(SoulForge.MOD_ID, "textures/projectiles/determination_arrow.png");

    public DeterminationArrowRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public Identifier getTexture(DeterminationArrowProjectile determinationArrow) { return TEXTURE; }
}
