package com.pulsar.soulforge.client.item;

import com.pulsar.soulforge.item.TraitedArniciteCoreItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TraitedArniciteCoreItemRenderer extends GeoItemRenderer<TraitedArniciteCoreItem> {
    public TraitedArniciteCoreItemRenderer() {
        super(new TraitedArniciteCoreItemModel());
    }

    @Override
    public RenderLayer getRenderType(TraitedArniciteCoreItem animatable, Identifier texture,
                                     VertexConsumerProvider bufferSource,
                                     float partialTick) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}
