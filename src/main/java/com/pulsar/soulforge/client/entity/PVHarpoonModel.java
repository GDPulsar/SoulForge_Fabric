package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.PVHarpoonProjectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class PVHarpoonModel extends GeoModel<PVHarpoonProjectile> {
    @Override
    public Identifier getModelResource(PVHarpoonProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/harpoon.geo.json");
    }

    @Override
    public Identifier getTextureResource(PVHarpoonProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/perseverance.png");
    }

    @Override
    public Identifier getAnimationResource(PVHarpoonProjectile animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(PVHarpoonProjectile animatable, long instanceId, AnimationState<PVHarpoonProjectile> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");

        if (bone != null) {
            bone.setRotX(animatable.getPitch() * MathHelper.RADIANS_PER_DEGREE);
            bone.setRotY(animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
