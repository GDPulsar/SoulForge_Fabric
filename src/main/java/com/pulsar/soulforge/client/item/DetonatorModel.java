package com.pulsar.soulforge.client.item;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.item.devices.devices.RevivalIdol;
import com.pulsar.soulforge.item.devices.machines.Detonator;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DetonatorModel extends GeoModel<Detonator> {
    @Override
    public Identifier getModelResource(Detonator animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/item/detonator.geo.json");
    }

    @Override
    public Identifier getTextureResource(Detonator animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/detonator.png");
    }

    @Override
    public Identifier getAnimationResource(Detonator animatable) {
        return null;
    }
}
