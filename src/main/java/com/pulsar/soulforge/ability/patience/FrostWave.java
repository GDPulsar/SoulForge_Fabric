package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.WeaponAbilityBase;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.Item;

public class FrostWave extends WeaponAbilityBase {
    @Override
    public Item getItem() {
        return SoulForgeItems.FROST_WAVE;
    }

    public int getLV() { return 3; }

    @Override
    public AbilityBase getInstance() {
        return new FrostWave();
    }
}
