package com.pulsar.soulforge.client.item;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.item.TraitedArniciteCoreItem;
import com.pulsar.soulforge.item.devices.devices.RevivalIdol;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class RevivalIdolModel extends GeoModel<RevivalIdol> {
    @Override
    public Identifier getModelResource(RevivalIdol animatable) {
        return Identifier.of(SoulForge.MOD_ID, "geo/item/revival_idol.geo.json");
    }

    @Override
    public Identifier getTextureResource(RevivalIdol animatable) {
        return Identifier.of(SoulForge.MOD_ID, "textures/item/revival_idol.png");
    }

    @Override
    public Identifier getAnimationResource(RevivalIdol animatable) {
        return null;
    }
}
