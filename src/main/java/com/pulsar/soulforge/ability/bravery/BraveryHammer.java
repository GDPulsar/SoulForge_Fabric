package com.pulsar.soulforge.ability.bravery;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class BraveryHammer extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.setWeapon(new ItemStack(SoulForgeItems.BRAVERY_HAMMER));
        return super.cast(player);
    }

    public String getName() { return "Bravery Hammer"; }

    public int getLV() { return 10; }

    public int getCost() { return 20; }

    public int getCooldown() { return 0; }

    @Override
    public AbilityType getType() { return AbilityType.WEAPON; }

    @Override
    public AbilityBase getInstance() {
        return new BraveryHammer();
    }
}
