package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.ShieldShardEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class ShieldShardModel extends GeoModel<ShieldShardEntity> {
    @Override
    public Identifier getModelResource(ShieldShardEntity animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/shield_shard.geo.json");
    }

    @Override
    public Identifier getTextureResource(ShieldShardEntity animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/integrity.png");
    }

    @Override
    public Identifier getAnimationResource(ShieldShardEntity animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(ShieldShardEntity animatable, long instanceId, AnimationState<ShieldShardEntity> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");

        if (bone != null) {
            bone.setRotX((0.9f * (animatable.age + animationState.getPartialTick()) + animatable.getPitch()) * MathHelper.RADIANS_PER_DEGREE);
            bone.setRotY((6.9f * (animatable.age + animationState.getPartialTick()) + animatable.getYaw()) * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
