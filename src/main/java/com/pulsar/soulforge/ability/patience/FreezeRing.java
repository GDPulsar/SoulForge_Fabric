package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.WeaponAbilityBase;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.Item;

public class FreezeRing extends WeaponAbilityBase {
    @Override
    public Item getItem() {
        return SoulForgeItems.FREEZE_RING;
    }

    public int getLV() { return 10; }

    @Override
    public AbilityBase getInstance() {
        return new FreezeRing();
    }
}
