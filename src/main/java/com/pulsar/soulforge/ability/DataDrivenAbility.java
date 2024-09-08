package com.pulsar.soulforge.ability;

import net.minecraft.util.Identifier;

public class DataDrivenAbility extends AbilityBase {
    public Identifier id;
    public String name;
    public int unlockLv;
    public int cost;
    public int cooldown;
    public AbilityType type;

    public DataDrivenAbility(Identifier id, String name, int unlockLv, int cost, int cooldown, AbilityType type) {
        this.id = id;
        this.name = name;
        this.unlockLv = unlockLv;
        this.cost = cost;
        this.cooldown = cooldown;
        this.type = type;
    }

    public Identifier getID() {
        return this.id;
    }

    @Override
    public int getLV() {
        return this.unlockLv;
    }

    @Override
    public int getCost() {
        return this.cost;
    }

    @Override
    public int getCooldown() {
        return this.cooldown;
    }

    @Override
    public AbilityType getType() {
        return this.type;
    }

    @Override
    public AbilityBase getInstance() {
        return new DataDrivenAbility(this.id, this.name, this.unlockLv, this.cost, this.cooldown, this.type);
    }
}
