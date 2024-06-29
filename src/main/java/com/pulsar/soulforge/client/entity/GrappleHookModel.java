package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.AntihealDartProjectile;
import com.pulsar.soulforge.entity.GrappleHookProjectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class GrappleHookModel extends GeoModel<GrappleHookProjectile> {
    @Override
    public Identifier getModelResource(GrappleHookProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/spear.geo.json");
    }

    @Override
    public Identifier getTextureResource(GrappleHookProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/perseverance.png");
    }

    @Override
    public Identifier getAnimationResource(GrappleHookProjectile animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(GrappleHookProjectile animatable, long instanceId, AnimationState<GrappleHookProjectile> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");

        if (bone != null) {
            bone.setRotX(animatable.getPitch() * MathHelper.RADIANS_PER_DEGREE);
            bone.setRotY(animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
