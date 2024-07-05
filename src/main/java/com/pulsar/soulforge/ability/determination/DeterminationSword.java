package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.WeaponAbilityBase;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.Item;

public class DeterminationSword extends WeaponAbilityBase {
    @Override
    public Item getItem() {
        return SoulForgeItems.DETERMINATION_SWORD;
    }

    public int getLV() { return 1; }

    @Override
    public AbilityBase getInstance() {
        return new DeterminationSword();
    }
}
