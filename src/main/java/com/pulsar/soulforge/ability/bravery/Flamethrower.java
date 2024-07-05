package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.WeaponAbilityBase;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.Item;

public class Flamethrower extends WeaponAbilityBase {
    @Override
    public Item getItem() {
        return SoulForgeItems.FLAMETHROWER;
    }

    @Override
    public int getCost() { return 0; }

    public int getLV() { return 5; }

    @Override
    public AbilityBase getInstance() {
        return new Flamethrower();
    }
}
