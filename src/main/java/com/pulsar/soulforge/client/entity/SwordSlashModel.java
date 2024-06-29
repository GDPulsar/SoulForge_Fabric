package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.BigSlashProjectile;
import com.pulsar.soulforge.entity.SwordSlashProjectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class SwordSlashModel extends GeoModel<SwordSlashProjectile> {
    @Override
    public Identifier getModelResource(SwordSlashProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/sword_slash.geo.json");
    }

    @Override
    public Identifier getTextureResource(SwordSlashProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/white.png");
    }

    @Override
    public Identifier getAnimationResource(SwordSlashProjectile animatable) {
        return null;
    }
    @Override
    public void setCustomAnimations(SwordSlashProjectile animatable, long instanceId, AnimationState<SwordSlashProjectile> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("bone");

        if (head != null) {
            head.setRotY(-animatable.getYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
