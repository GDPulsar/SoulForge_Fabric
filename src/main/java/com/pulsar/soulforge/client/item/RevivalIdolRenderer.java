package com.pulsar.soulforge.client.item;

import com.pulsar.soulforge.item.TraitedArniciteCoreItem;
import com.pulsar.soulforge.item.devices.devices.RevivalIdol;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RevivalIdolRenderer extends GeoItemRenderer<RevivalIdol> {
    public RevivalIdolRenderer() {
        super(new RevivalIdolModel());
    }
}
