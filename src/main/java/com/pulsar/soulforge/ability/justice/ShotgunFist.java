package com.pulsar.soulforge.ability.justice;

import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.WeaponAbilityBase;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.Item;

public class ShotgunFist extends WeaponAbilityBase {
    @Override
    public Item getItem() {
        return SoulForgeItems.SHOTGUN_FIST;
    }

    public int getCooldown() { return 200; }

    public int getLV() { return 3; }

    @Override
    public AbilityBase getInstance() {
        return new ShotgunFist();
    }
}
