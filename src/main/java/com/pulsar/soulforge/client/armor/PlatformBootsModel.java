package com.pulsar.soulforge.client.armor;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.armor.PlatformBootsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PlatformBootsModel extends GeoModel<PlatformBootsItem> {
    @Override
    public Identifier getModelResource(PlatformBootsItem animatable) {
        return Identifier.of(SoulForge.MOD_ID, "geo/armor/platform_boots.geo.json");
    }

    @Override
    public Identifier getTextureResource(PlatformBootsItem animatable) {
        return Identifier.of(SoulForge.MOD_ID, "textures/armor/platform_boots.png");
    }

    @Override
    public Identifier getAnimationResource(PlatformBootsItem animatable) {
        return null;
    }
}
