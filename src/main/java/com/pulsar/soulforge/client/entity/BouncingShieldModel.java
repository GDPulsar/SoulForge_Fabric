package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.BouncingShieldEntity;
import com.pulsar.soulforge.entity.BraverySpearProjectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BouncingShieldModel extends GeoModel<BouncingShieldEntity> {
    @Override
    public Identifier getModelResource(BouncingShieldEntity animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/shield.geo.json");
    }

    @Override
    public Identifier getTextureResource(BouncingShieldEntity animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/justice.png");
    }

    @Override
    public Identifier getAnimationResource(BouncingShieldEntity animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(BouncingShieldEntity animatable, long instanceId, AnimationState<BouncingShieldEntity> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");

        if (bone != null) {
            bone.setRotX(animatable.getPitch() * MathHelper.RADIANS_PER_DEGREE);
            bone.setRotY(animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
