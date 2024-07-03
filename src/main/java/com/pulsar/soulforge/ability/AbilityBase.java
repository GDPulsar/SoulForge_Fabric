package com.pulsar.soulforge.ability;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class AbilityBase {
    public abstract String getName();
    public abstract Text getLocalizedText();
    public abstract Identifier getID();
    public abstract String getTooltip();
    public abstract int getLV();
    public abstract int getCost();
    public abstract int getCooldown();
    public abstract AbilityType getType();
    public abstract AbilityBase getInstance();

    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.putString("id", getID().getPath());
        nbt.putString("name", getName());
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {

    }

    public abstract boolean cast(ServerPlayerEntity player);
    public abstract boolean tick(ServerPlayerEntity player);
    public abstract boolean end(ServerPlayerEntity player);
    public void displayTick(ClientPlayerEntity player) {};
}
