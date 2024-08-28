package com.pulsar.soulforge.client.block;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.block.SoulForgeBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SoulForgeBlockModel extends GeoModel<SoulForgeBlockEntity> {
    @Override
    public Identifier getModelResource(SoulForgeBlockEntity animatable) {
        return new Identifier(SoulForge.MOD_ID, "geo/block/soul_forge.geo.json");
    }

    @Override
    public Identifier getTextureResource(SoulForgeBlockEntity animatable) {
        return new Identifier(SoulForge.MOD_ID, "textures/block/soul_forge.png");
    }

    @Override
    public Identifier getAnimationResource(SoulForgeBlockEntity animatable) {
        return new Identifier(SoulForge.MOD_ID, "animations/soul_forge.animation.json");
    }
}
