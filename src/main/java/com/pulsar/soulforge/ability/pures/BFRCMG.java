package com.pulsar.soulforge.ability.pures;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.WeaponAbilityBase;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.Item;

public class BFRCMG extends WeaponAbilityBase {
    @Override
    public Item getItem() {
        return SoulForgeItems.BFRCMG;
    }

    public int getLV() { return 10; }

    public int getCooldown() { return 200; }

    @Override
    public AbilityBase getInstance() {
        return new BFRCMG();
    }
}
