package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class Flamethrower extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.setWeapon(new ItemStack(SoulForgeItems.FLAMETHROWER));
        return super.cast(player);
    }

    public String getName() { return "Flamethrower"; }

    public int getLV() { return 5; }

    public int getCost() { return 0; }

    public int getCooldown() { return 0; }

    @Override
    public AbilityType getType() { return AbilityType.WEAPON; }

    @Override
    public AbilityBase getInstance() {
        return new Flamethrower();
    }
}
