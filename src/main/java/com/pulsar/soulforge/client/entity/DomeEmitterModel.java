package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.BraverySpearProjectile;
import com.pulsar.soulforge.entity.DomeEmitterEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class DomeEmitterModel extends GeoModel<DomeEmitterEntity> {
    @Override
    public Identifier getModelResource(DomeEmitterEntity animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/item/dome_emitter.geo.json");
    }

    @Override
    public Identifier getTextureResource(DomeEmitterEntity animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/dome_emitter.png");
    }

    @Override
    public Identifier getAnimationResource(DomeEmitterEntity animatable) {
        return null;
    }
}
