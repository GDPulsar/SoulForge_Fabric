package com.pulsar.soulforge.client.armor;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.armor.CatEarsItem;
import com.pulsar.soulforge.armor.PlatformBootsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CatEarsModel extends GeoModel<CatEarsItem> {
    @Override
    public Identifier getModelResource(CatEarsItem animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/armor/cat_ears.geo.json");
    }

    @Override
    public Identifier getTextureResource(CatEarsItem animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/armor/cat_ears_1.png");
    }

    @Override
    public Identifier getAnimationResource(CatEarsItem animatable) {
        return new Identifier(SoulForge.MOD_ID, "animations/cat_ears.animation.json");
    }
}
