package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.entity.SnowgraveProjectile;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class IceWallModel extends GeoModel<SnowgraveProjectile> {
    @Override
    public Identifier getModelResource(SnowgraveProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/ice_wall.geo.json");
    }

    @Override
    public Identifier getTextureResource(SnowgraveProjectile animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/patience.png");
    }

    @Override
    public Identifier getAnimationResource(SnowgraveProjectile animatable) {
        return null;
    }
}
