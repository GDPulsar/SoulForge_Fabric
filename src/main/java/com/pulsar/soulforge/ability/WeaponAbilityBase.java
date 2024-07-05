package com.pulsar.soulforge.ability;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.components.SoulComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class WeaponAbilityBase extends AbilityBase {
    public abstract Item getItem();

    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.setWeapon(new ItemStack(getItem()));
        return true;
    }

    public int getCost() { return 20; }

    public int getCooldown() { return 0; }

    public AbilityType getType() { return AbilityType.WEAPON; }
}
