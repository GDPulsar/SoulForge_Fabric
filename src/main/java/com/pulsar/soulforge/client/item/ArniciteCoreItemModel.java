package com.pulsar.soulforge.client.item;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.item.ArniciteCoreItem;
import com.pulsar.soulforge.item.TraitedArniciteCoreItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ArniciteCoreItemModel extends GeoModel<ArniciteCoreItem> {
    @Override
    public Identifier getModelResource(ArniciteCoreItem animatable) {
        return Identifier.of(SoulForge.MOD_ID, "geo/item/core.geo.json");
    }

    @Override
    public Identifier getTextureResource(ArniciteCoreItem animatable) {
        return Identifier.of(SoulForge.MOD_ID, "textures/item/arnicite_core.png");
    }

    @Override
    public Identifier getAnimationResource(ArniciteCoreItem animatable) {
        return Identifier.of(SoulForge.MOD_ID, "animations/core.animation.json");
    }
}
