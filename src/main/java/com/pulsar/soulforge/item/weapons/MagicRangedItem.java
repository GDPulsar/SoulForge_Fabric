package com.pulsar.soulforge.item.weapons;

import com.pulsar.soulforge.item.weapons.MagicItem;

public abstract class MagicRangedItem extends MagicItem {
    public MagicRangedItem() {
        super();
    }

    public int getEnchantability() {
        return 1;
    }

    public abstract int getRange();
}
