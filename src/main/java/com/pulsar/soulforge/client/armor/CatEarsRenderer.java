package com.pulsar.soulforge.client.armor;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.armor.CatEarsItem;
import com.pulsar.soulforge.armor.PlatformBootsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class CatEarsRenderer extends GeoArmorRenderer<CatEarsItem> {
    public CatEarsRenderer() {
        super(new DefaultedItemGeoModel<>(new Identifier(SoulForge.MOD_ID, "armor/cat_ears")));
    }
}
