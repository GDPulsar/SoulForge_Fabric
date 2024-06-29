package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.BraverySpearProjectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BraverySpearModel extends GeoModel<BraverySpearProjectile> {
    @Override
    public Identifier getModelResource(BraverySpearProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/spear.geo.json");
    }

    @Override
    public Identifier getTextureResource(BraverySpearProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/bravery.png");
    }

    @Override
    public Identifier getAnimationResource(BraverySpearProjectile animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(BraverySpearProjectile animatable, long instanceId, AnimationState<BraverySpearProjectile> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");

        if (bone != null) {
            bone.setRotX(animatable.getPitch() * MathHelper.RADIANS_PER_DEGREE);
            bone.setRotY(animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
