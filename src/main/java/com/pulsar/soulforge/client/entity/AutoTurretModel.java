package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.AutoTurretEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class AutoTurretModel extends GeoModel<AutoTurretEntity> {
    @Override
    public Identifier getModelResource(AutoTurretEntity animatable) {
        return Identifier.of(SoulForge.MOD_ID, "geo/turret.geo.json");
    }

    @Override
    public Identifier getTextureResource(AutoTurretEntity animatable) {
        return Identifier.of(SoulForge.MOD_ID, "textures/item/justice.png");
    }

    @Override
    public Identifier getAnimationResource(AutoTurretEntity animatable) {
        return null;
    }
    @Override
    public void setCustomAnimations(AutoTurretEntity animatable, long instanceId, AnimationState<AutoTurretEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            head.setRotX(animatable.getPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
