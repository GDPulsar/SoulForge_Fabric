package com.pulsar.soulforge.client.armor;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.armor.PlatformBootsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class PlatformBootsRenderer extends GeoArmorRenderer<PlatformBootsItem> {
    public PlatformBootsRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.of(SoulForge.MOD_ID, "armor/platform_boots")));
    }
}
