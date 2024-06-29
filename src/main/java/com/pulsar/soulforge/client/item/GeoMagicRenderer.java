package com.pulsar.soulforge.client.item;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GeoMagicRenderer<T extends Item & GeoAnimatable> extends GeoItemRenderer<T> {
    public GeoMagicRenderer(String itemID, String textureID, String animationID) {
        super(new GeoMagicItemModel<>(
                new Identifier(SoulForge.MOD_ID, "geo/" + itemID + ".geo.json"),
                new Identifier(SoulForge.MOD_ID, "textures/" + textureID + ".png"),
                new Identifier(SoulForge.MOD_ID, "animations/" + animationID + ".animation.json")
        ));
    }
}
