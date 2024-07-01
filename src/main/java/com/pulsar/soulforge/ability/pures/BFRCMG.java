package com.pulsar.soulforge.ability.pures;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import com.pulsar.soulforge.components.SoulComponent;
import com.pulsar.soulforge.item.SoulForgeItems;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BFRCMG extends AbilityBase {
    @Override
    public boolean cast(ServerPlayerEntity player) {
        SoulComponent playerSoul = SoulForge.getPlayerSoul(player);
        if (!playerSoul.hasWeapon() || !playerSoul.getWeapon().isOf(SoulForgeItems.BFRCMG)) {
            playerSoul.setWeapon(new ItemStack(SoulForgeItems.BFRCMG));
            return super.cast(player);
        }
        return false;
    }

    public String getName() { return "BFRCMG"; }
    public Identifier getId() { return Identifier.of(SoulForge.MOD_ID, "bfrcmg"); }
    public int getLV() { return 10; }
    public int getCost() { return 20; }
    public int getCooldown() { return 100; }
    public AbilityType getType() { return AbilityType.WEAPON; }

    @Override
    public AbilityBase getInstance() {
        return new BFRCMG();
    }
}
