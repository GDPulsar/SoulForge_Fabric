package com.pulsar.soulforge.client.item;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GeoMagicItemRenderer<T extends Item & GeoAnimatable> extends GeoItemRenderer<T> {
    public GeoMagicItemRenderer(String itemID, String textureID) {
        super(new GeoMagicItemModel<>(Identifier.of(SoulForge.MOD_ID, "geo/item/" + itemID + ".geo.json"), Identifier.of(SoulForge.MOD_ID, "textures/item/" + textureID + ".png")));
    }

    public GeoMagicItemRenderer(String itemID, String textureID, String animID) {
        super(new GeoMagicItemModel<>(
                Identifier.of(SoulForge.MOD_ID, "geo/item/" + itemID + ".geo.json"),
                Identifier.of(SoulForge.MOD_ID, "textures/item/" + textureID + ".png"),
                Identifier.of(SoulForge.MOD_ID, "animations/item/" + animID + ".animation.json")
        ));
    }

    @Override
    public RenderLayer getRenderType(T animatable, Identifier texture,
                                     VertexConsumerProvider bufferSource,
                                     float partialTick) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}
