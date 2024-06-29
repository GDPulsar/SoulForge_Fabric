package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.BigSlashProjectile;
import com.pulsar.soulforge.entity.SmallSlashProjectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BigSlashModel extends GeoModel<BigSlashProjectile> {
    @Override
    public Identifier getModelResource(BigSlashProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/static_slash_big.geo.json");
    }

    @Override
    public Identifier getTextureResource(BigSlashProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/determination.png");
    }

    @Override
    public Identifier getAnimationResource(BigSlashProjectile animatable) {
        return null;
    }
    @Override
    public void setCustomAnimations(BigSlashProjectile animatable, long instanceId, AnimationState<BigSlashProjectile> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("bone");

        if (head != null) {
            head.setRotX(animatable.getPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
