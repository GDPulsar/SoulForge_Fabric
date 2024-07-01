package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.SnowgraveProjectile;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class SnowgraveModel extends GeoModel<SnowgraveProjectile> {
    @Override
    public Identifier getModelResource(SnowgraveProjectile animatable) {
        return Identifier.of(SoulForge.MOD_ID, "geo/snowgrave.geo.json");
    }

    @Override
    public Identifier getTextureResource(SnowgraveProjectile animatable) {
        return Identifier.of(SoulForge.MOD_ID, "textures/entity/snowgrave.png");
    }

    @Override
    public Identifier getAnimationResource(SnowgraveProjectile animatable) {
        return Identifier.of(SoulForge.MOD_ID, "animations/snowgrave.animation.json");
    }
}
