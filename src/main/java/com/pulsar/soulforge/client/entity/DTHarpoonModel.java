package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.DTHarpoonProjectile;
import com.pulsar.soulforge.entity.PVHarpoonProjectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class DTHarpoonModel extends GeoModel<DTHarpoonProjectile> {
    @Override
    public Identifier getModelResource(DTHarpoonProjectile animatable) {
        return Identifier.of(SoulForge.MOD_ID, "geo/harpoon.geo.json");
    }

    @Override
    public Identifier getTextureResource(DTHarpoonProjectile animatable) {
        return Identifier.of(SoulForge.MOD_ID, "textures/item/determination.png");
    }

    @Override
    public Identifier getAnimationResource(DTHarpoonProjectile animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(DTHarpoonProjectile animatable, long instanceId, AnimationState<DTHarpoonProjectile> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");

        if (bone != null) {
            bone.setRotX(animatable.getPitch() * MathHelper.RADIANS_PER_DEGREE);
            bone.setRotY(animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
