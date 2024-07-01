package com.pulsar.soulforge.client.item;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.item.TraitedArniciteCoreItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class TraitedArniciteCoreItemModel extends GeoModel<TraitedArniciteCoreItem> {
    @Override
    public Identifier getModelResource(TraitedArniciteCoreItem animatable) {
        return Identifier.of(SoulForge.MOD_ID, "geo/item/core.geo.json");
    }

    @Override
    public Identifier getTextureResource(TraitedArniciteCoreItem animatable) {
        return Identifier.of(SoulForge.MOD_ID, "textures/item/" + animatable.trait.getName().toLowerCase() + "_core.png");
    }

    @Override
    public Identifier getAnimationResource(TraitedArniciteCoreItem animatable) {
        return Identifier.of(SoulForge.MOD_ID, "animations/core.animation.json");
    }
}
