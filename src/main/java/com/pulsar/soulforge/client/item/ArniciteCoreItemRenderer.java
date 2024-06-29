package com.pulsar.soulforge.client.item;

import com.pulsar.soulforge.item.ArniciteCoreItem;
import com.pulsar.soulforge.item.TraitedArniciteCoreItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ArniciteCoreItemRenderer extends GeoItemRenderer<ArniciteCoreItem> {
    public ArniciteCoreItemRenderer() {
        super(new ArniciteCoreItemModel());
    }

    @Override
    public RenderLayer getRenderType(ArniciteCoreItem animatable, Identifier texture,
                                     VertexConsumerProvider bufferSource,
                                     float partialTick) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}
