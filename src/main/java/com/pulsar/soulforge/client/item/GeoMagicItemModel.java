package com.pulsar.soulforge.client.item;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class GeoMagicItemModel<T extends Item & GeoAnimatable> extends GeoModel<T> {
    final Identifier modelID;
    final Identifier textureID;
    final Identifier animationID;

    public GeoMagicItemModel(Identifier model, Identifier texture) {
        this.modelID = model;
        this.textureID = texture;
        this.animationID = null;
    }

    public GeoMagicItemModel(Identifier model, Identifier texture, Identifier animation) {
        this.modelID = model;
        this.textureID = texture;
        this.animationID = animation;
    }

    @Override
    public Identifier getModelResource(T animatable) {
        return modelID;
    }

    @Override
    public Identifier getTextureResource(T animatable) {
        return textureID;
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return animationID;
    }
}
