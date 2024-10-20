package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.SkullProjectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class SkullProjectileModel extends GeoModel<SkullProjectile> {
    @Override
    public Identifier getModelResource(SkullProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/skull.geo.json");
    }

    @Override
    public Identifier getTextureResource(SkullProjectile animatable) {
        return new Identifier("textures/block/bone_block_side.png");
    }

    @Override
    public Identifier getAnimationResource(SkullProjectile animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(SkullProjectile animatable, long instanceId, AnimationState<SkullProjectile> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");

        if (bone != null) {
            bone.setRotX(-animatable.getPitch() * MathHelper.RADIANS_PER_DEGREE);
            bone.setRotY(-animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
