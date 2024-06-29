package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.AutoTurretEntity;
import com.pulsar.soulforge.entity.SmallSlashProjectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class SmallSlashModel extends GeoModel<SmallSlashProjectile> {
    @Override
    public Identifier getModelResource(SmallSlashProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/static_slash_small.geo.json");
    }

    @Override
    public Identifier getTextureResource(SmallSlashProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/determination.png");
    }

    @Override
    public Identifier getAnimationResource(SmallSlashProjectile animatable) {
        return null;
    }
    @Override
    public void setCustomAnimations(SmallSlashProjectile animatable, long instanceId, AnimationState<SmallSlashProjectile> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("bone");

        if (head != null) {
            head.setRotX(animatable.getPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
