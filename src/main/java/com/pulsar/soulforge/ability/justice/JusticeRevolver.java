package com.pulsar.soulforge.ability.justice;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.WeaponAbilityBase;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class JusticeRevolver extends WeaponAbilityBase {
    @Override
    public Item getItem() {
        return SoulForgeItems.JUSTICE_REVOLVER;
    }

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        ItemStack stack = new ItemStack(getItem());
        stack.getOrCreateNbt().putInt("ammo", 6 + playerSoul.getLV());
        playerSoul.setWeapon(stack);
        return true;
    }

    public int getLV() { return 10; }

    @Override
    public AbilityBase getInstance() {
        return new JusticeRevolver();
    }
}
