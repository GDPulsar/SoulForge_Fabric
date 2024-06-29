package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.AutoTurretEntity;
import com.pulsar.soulforge.entity.FireTornadoProjectile;
import com.pulsar.soulforge.entity.SnowgraveProjectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class FireTornadoModel extends GeoModel<FireTornadoProjectile> {
    @Override
    public Identifier getModelResource(FireTornadoProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/fire_tornado.geo.json");
    }

    @Override
    public Identifier getTextureResource(FireTornadoProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/bravery.png");
    }

    @Override
    public Identifier getAnimationResource(FireTornadoProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "animations/fire_tornado.animation.json");
    }

    @Override
    public void setCustomAnimations(FireTornadoProjectile animatable, long instanceId, AnimationState<FireTornadoProjectile> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("base");
        if (bone != null) {
            float timer = (float) animatable.age / 20f;
            float size = 2.5f + 15f * Math.min(timer, 45f) / 45f;
            float height = 30f + 40f * Math.min(timer, 45f) / 45f;
            bone.updateScale(size, height, size);
        }
    }
}
