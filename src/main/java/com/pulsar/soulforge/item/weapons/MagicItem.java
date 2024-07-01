package com.pulsar.soulforge.item.weapons;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.item.Item;

public class MagicItem extends Item {
    public MagicItem() {
        super(new Item.Settings().maxCount(1).component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true)));
    }

    public MagicItem(Item.Settings settings) {
        super(settings.maxCount(1).component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true)));
    }
}
