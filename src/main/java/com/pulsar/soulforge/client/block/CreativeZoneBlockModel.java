package com.pulsar.soulforge.client.block;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.block.CreativeZoneBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CreativeZoneBlockModel extends GeoModel<CreativeZoneBlockEntity> {
    @Override
    public Identifier getModelResource(CreativeZoneBlockEntity animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/block/creative_zone.geo.json");
    }

    @Override
    public Identifier getTextureResource(CreativeZoneBlockEntity animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/block/creative_zone.png");
    }

    @Override
    public Identifier getAnimationResource(CreativeZoneBlockEntity animatable) {
        return new Identifier(SoulForge.MOD_ID, "animations/creative_zone.animation.json");
    }
}
