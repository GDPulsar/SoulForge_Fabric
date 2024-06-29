package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.DeterminationSpearProjectile;
import com.pulsar.soulforge.entity.SOJProjectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class SOJModel extends GeoModel<SOJProjectile> {
    @Override
    public Identifier getModelResource(SOJProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/soj.geo.json");
    }

    @Override
    public Identifier getTextureResource(SOJProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/determination.png");
    }

    @Override
    public Identifier getAnimationResource(SOJProjectile animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(SOJProjectile animatable, long instanceId, AnimationState<SOJProjectile> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");

        if (bone != null) {
            bone.setRotX(animatable.getPitch() * MathHelper.RADIANS_PER_DEGREE);
            bone.setRotY(animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
