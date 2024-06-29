package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.BraverySpearProjectile;
import com.pulsar.soulforge.entity.IncendiaryGrenadeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class IncendiaryGrenadeModel extends GeoModel<IncendiaryGrenadeEntity> {
    @Override
    public Identifier getModelResource(IncendiaryGrenadeEntity animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/incendiary_grenade.geo.json");
    }

    @Override
    public Identifier getTextureResource(IncendiaryGrenadeEntity animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/incendiary_grenade.png");
    }

    @Override
    public Identifier getAnimationResource(IncendiaryGrenadeEntity animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(IncendiaryGrenadeEntity animatable, long instanceId, AnimationState<IncendiaryGrenadeEntity> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");

        if (bone != null) {
            bone.setRotX(animatable.getPitch() * MathHelper.RADIANS_PER_DEGREE);
            bone.setRotY(animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
