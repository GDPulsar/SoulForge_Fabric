package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.DTHarpoonProjectile;
import com.pulsar.soulforge.entity.JusticeHarpoonProjectile;
import com.pulsar.soulforge.item.weapons.JusticeHarpoon;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class JusticeHarpoonModel extends GeoModel<JusticeHarpoonProjectile> {
    @Override
    public Identifier getModelResource(JusticeHarpoonProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/harpoon.geo.json");
    }

    @Override
    public Identifier getTextureResource(JusticeHarpoonProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/perseverance.png");
    }

    @Override
    public Identifier getAnimationResource(JusticeHarpoonProjectile animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(JusticeHarpoonProjectile animatable, long instanceId, AnimationState<JusticeHarpoonProjectile> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");

        if (bone != null) {
            bone.setRotX(animatable.getPitch() * MathHelper.RADIANS_PER_DEGREE);
            bone.setRotY(animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
