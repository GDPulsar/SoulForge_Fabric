package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.AntihealDartProjectile;
import com.pulsar.soulforge.entity.BraverySpearProjectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class AntihealDartModel extends GeoModel<AntihealDartProjectile> {
    @Override
    public Identifier getModelResource(AntihealDartProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/antiheal_dart.geo.json");
    }

    @Override
    public Identifier getTextureResource(AntihealDartProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/perseverance.png");
    }

    @Override
    public Identifier getAnimationResource(AntihealDartProjectile animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(AntihealDartProjectile animatable, long instanceId, AnimationState<AntihealDartProjectile> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");

        if (bone != null) {
            bone.setRotX(animatable.getPitch() * MathHelper.RADIANS_PER_DEGREE);
            bone.setRotY(animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
