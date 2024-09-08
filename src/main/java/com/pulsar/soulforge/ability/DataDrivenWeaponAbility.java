package com.pulsar.soulforge.ability;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class DataDrivenWeaponAbility extends WeaponAbilityBase {
    public Identifier id;
    public String name;
    public int unlockLv;
    public int cost;
    public int cooldown;
    public Item item;

    @Override
    public Item getItem() {
        return null;
    }

    public DataDrivenWeaponAbility(Identifier id, String name, int unlockLv, int cost, int cooldown, Item item) {
        this.id = id;
        this.name = name;
        this.unlockLv = unlockLv;
        this.cost = cost;
        this.cooldown = cooldown;
        this.item = item;
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
        return new DataDrivenWeaponAbility(this.id, this.name, this.unlockLv, this.cost, this.cooldown, this.item);
    }
}
