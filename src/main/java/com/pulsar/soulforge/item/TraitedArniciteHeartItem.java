package com.pulsar.soulforge.item;

import com.pulsar.soulforge.trait.TraitBase;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

public class TraitedArniciteHeartItem extends Item {
    public final TraitBase trait;

    public TraitedArniciteHeartItem(TraitBase trait) {
        super(new FabricItemSettings());
        this.trait = trait;
    }
}
