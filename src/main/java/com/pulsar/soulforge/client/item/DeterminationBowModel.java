package com.pulsar.soulforge.client.item;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.item.weapons.weapon_wheel.DeterminationBow;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DeterminationBowModel extends GeoModel<DeterminationBow> {
    @Override
    public Identifier getModelResource(DeterminationBow animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/item/determination_bow.geo.json");
    }

    @Override
    public Identifier getTextureResource(DeterminationBow animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/item/determination.png");
    }

    @Override
    public Identifier getAnimationResource(DeterminationBow animatable) {
        return null;
    }
}
