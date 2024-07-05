package com.pulsar.soulforge.ability.determination;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.ability.AbilityBase;
import com.pulsar.soulforge.ability.AbilityType;
import net.minecraft.util.Identifier;

public class TrueLOVE extends AbilityBase {
    
    public String getName() { return "True LOVE"; }

    public Identifier getID() { return new Identifier(SoulForge.MOD_ID, "true_love"); }

    public int getLV() { return 1; }

    public int getCost() { return 0; }

    public int getCooldown() { return 0; }

    public AbilityType getType() { return AbilityType.PASSIVE; }

    @Override
    public AbilityBase getInstance() {
        return new TrueLOVE();
    }
}
