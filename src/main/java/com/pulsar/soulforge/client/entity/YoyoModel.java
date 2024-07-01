package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.AntihealDartProjectile;
import com.pulsar.soulforge.entity.YoyoProjectile;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class YoyoModel extends GeoModel<YoyoProjectile> {
    @Override
    public Identifier getModelResource(YoyoProjectile animatable) {
        return Identifier.of(SoulForge.MOD_ID, "geo/yoyo.geo.json");
    }

    @Override
    public Identifier getTextureResource(YoyoProjectile animatable) {
        return Identifier.of(SoulForge.MOD_ID, "textures/item/integrity.png");
    }

    @Override
    public Identifier getAnimationResource(YoyoProjectile animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(YoyoProjectile animatable, long instanceId, AnimationState<YoyoProjectile> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");
    }
}
