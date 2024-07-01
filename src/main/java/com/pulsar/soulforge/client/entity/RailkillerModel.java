package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.AntihealDartProjectile;
import com.pulsar.soulforge.entity.BraverySpearProjectile;
import com.pulsar.soulforge.entity.RailkillerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class RailkillerModel extends GeoModel<RailkillerEntity> {
    @Override
    public Identifier getModelResource(RailkillerEntity animatable) {
        return Identifier.of(SoulForge.MOD_ID, "geo/railkiller.geo.json");
    }

    @Override
    public Identifier getTextureResource(RailkillerEntity animatable) {
        return Identifier.of(SoulForge.MOD_ID, "textures/item/railkiller.png");
    }

    @Override
    public Identifier getAnimationResource(RailkillerEntity animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(RailkillerEntity animatable, long instanceId, AnimationState<RailkillerEntity> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bb_main");

        if (bone != null) {
            bone.setRotY(animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
