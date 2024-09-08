package com.pulsar.soulforge.ability;

import net.minecraft.util.Identifier;

public class DataDrivenToggleableAbility extends ToggleableAbilityBase {
    public Identifier id;
    public String name;
    public int unlockLv;
    public int cost;
    public int cooldown;

    public DataDrivenToggleableAbility(Identifier id, String name, int unlockLv, int cost, int cooldown) {
        this.id = id;
        this.name = name;
        this.unlockLv = unlockLv;
        this.cost = cost;
        this.cooldown = cooldown;
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
    public AbilityBase getInstance() {
        return new DataDrivenToggleableAbility(this.id, this.name, this.unlockLv, this.cost, this.cooldown);
    }
}
