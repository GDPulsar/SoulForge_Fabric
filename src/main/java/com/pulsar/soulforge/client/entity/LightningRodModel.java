package com.pulsar.soulforge.client.entity;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.util.Identifier;

public class LightningRodModel extends TridentEntityModel {
    public static final Identifier TEXTURE = Identifier.of(SoulForge.MOD_ID, "textures/entity/lightning_rod");

    public LightningRodModel(ModelPart root) {
        super(root);
    }
}
