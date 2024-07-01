package com.pulsar.soulforge.client.item;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DefaultedGeoItemRenderer<T extends Item & GeoAnimatable> extends GeoItemRenderer<T> {
    public DefaultedGeoItemRenderer(String path) {
        super(new DefaultedItemGeoModel<>(Identifier.of(SoulForge.MOD_ID, path)));
    }

    @Override
    public RenderLayer getRenderType(T animatable, Identifier texture,
                                     VertexConsumerProvider bufferSource,
                                     float partialTick) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}
