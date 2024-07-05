package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.WeaponAbilityBase;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.Item;

public class BraveryHammer extends WeaponAbilityBase {
    @Override
    public Item getItem() {
        return SoulForgeItems.BRAVERY_HAMMER;
    }

    public int getLV() { return 10; }

    @Override
    public AbilityBase getInstance() {
        return new BraveryHammer();
    }
}
