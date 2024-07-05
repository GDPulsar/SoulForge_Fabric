package com.pulsar.soulforge.ability.justice;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.WeaponAbilityBase;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.Item;

public class JusticeRevolver extends WeaponAbilityBase {
    @Override
    public Item getItem() {
        return SoulForgeItems.JUSTICE_REVOLVER;
    }

    public int getLV() { return 10; }

    @Override
    public AbilityBase getInstance() {
        return new JusticeRevolver();
    }
}
