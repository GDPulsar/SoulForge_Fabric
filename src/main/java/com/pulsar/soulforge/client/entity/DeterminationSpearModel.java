package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.BraverySpearProjectile;
import com.pulsar.soulforge.entity.DeterminationSpearProjectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class DeterminationSpearModel extends GeoModel<DeterminationSpearProjectile> {
    @Override
    public Identifier getModelResource(DeterminationSpearProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/spear.geo.json");
    }

    @Override
    public Identifier getTextureResource(DeterminationSpearProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/determination.png");
    }

    @Override
    public Identifier getAnimationResource(DeterminationSpearProjectile animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(DeterminationSpearProjectile animatable, long instanceId, AnimationState<DeterminationSpearProjectile> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");

        if (bone != null) {
            bone.setRotX(animatable.getPitch() * MathHelper.RADIANS_PER_DEGREE);
            bone.setRotY(animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
