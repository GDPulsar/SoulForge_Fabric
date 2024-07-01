package com.pulsar.soulforge.ability;

import com.pulsar.soulforge.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public abstract class AbilityBase {
    boolean active = false;
    int lastCast = 0;
    boolean discovered = false;

    public String getName() {
        String name = this.getClass().getName();
        String spacedName = "";
        for (int i = 0; i < name.length(); i++) {
            spacedName += name.charAt(i);
            if (("" + name.charAt(i)).matches("[A-Z]")) {
                spacedName += " ";
            }
        }
        return spacedName;
    }
    public Text getLocalizedText() {
        return Text.translatable("ability."+getID().getPath()+".name");
    }
    public Identifier getID() {
        return Utils.convertAbilityNameToID(getName());
    }
    public String getTooltip() {
        return Text.translatable("ability."+getID().getPath()+".tooltip").getString();
    }
    public abstract int getLV();
    public abstract int getCost();
    public abstract int getCooldown();
    public abstract AbilityType getType();
    public abstract AbilityBase getInstance();

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public int getLastCast() {
        return lastCast;
    }
    public void setLastCast(int lastCast) {
        this.lastCast = lastCast;
    }
    public boolean isDiscovered() {
        return discovered;
    }
    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }
    public boolean isOnCooldown(int currentTicks) {
        return getLastCast() + getCooldown() >= currentTicks;
    }
    public float getCooldownPercent(int currentTicks) {
        return Math.clamp((float)(currentTicks - getLastCast()) / (float)getCooldown(), 0f, 1f);
    }

    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.putString("id", getID().getPath());
        nbt.putString("name", getName());
        nbt.putBoolean("active", isActive());
        nbt.putInt("lastCast", getLastCast());
        nbt.putBoolean("discovered", isDiscovered());
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        setActive(nbt.getBoolean("active"));
        setLastCast(nbt.getInt("lastCast"));
        setDiscovered(nbt.getBoolean("discovered"));
    }

    public boolean cast(ServerPlayerEntity player) {
        setActive(true);
        setLastCast(Objects.requireNonNull(player.getServer()).getTicks());
        return true;
    }
    public boolean tick(ServerPlayerEntity player) {
        return true;
    }
    public boolean end(ServerPlayerEntity player) {
        setActive(false);
        return true;
    }
    public void displayTick(PlayerEntity player) {

    }
}
