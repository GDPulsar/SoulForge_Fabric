package com.pulsar.soulforge.ability.kindness;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.WeaponAbilityBase;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.Item;

public class KindnessShield extends WeaponAbilityBase {
    @Override
    public Item getItem() {
        return SoulForgeItems.KINDNESS_SHIELD;
    }

    public int getLV() { return 3; }

    @Override
    public AbilityBase getInstance() {
        return new KindnessShield();
    }
}
