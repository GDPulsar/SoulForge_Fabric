package com.pulsar.soulforge.ability.patience;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class FreezeRing extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        playerSoul.setWeapon(new ItemStack(SoulForgeItems.FREEZE_RING));
        return super.cast(player);
    }

    public int getLV() { return 10; }
    public int getCost() { return 20; }
    public int getCooldown() { return 0; }
    public AbilityType getType() { return AbilityType.WEAPON; }

    @Override
    public AbilityBase getInstance() {
        return new FreezeRing();
    }
}
