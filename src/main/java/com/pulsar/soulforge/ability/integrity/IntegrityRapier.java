package com.pulsar.soulforge.ability.integrity;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.WeaponAbilityBase;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.Item;

public class IntegrityRapier extends WeaponAbilityBase {
    @Override
    public Item getItem() {
        return SoulForgeItems.INTEGRITY_RAPIER;
    }

    public int getLV() { return 10; }

    @Override
    public AbilityBase getInstance() {
        return new IntegrityRapier();
    }
}
