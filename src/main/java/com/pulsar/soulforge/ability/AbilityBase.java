package com.pulsar.soulforge.ability;

import com.pulsar.soulforge.SoulForge;
import com.pulsar.soulforge.util.CooldownDisplayEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public abstract class AbilityBase {
    private boolean isActive = false;
    private int cooldown = 0;

    public String getName() {
        return String.join(" ", this.getClass().getSimpleName().split("(?=\\p{Upper})"));
    }
    public Text getLocalizedText() { return Text.translatable("ability."+getID().getPath()+".name"); }
    public Identifier getID() { return new Identifier(SoulForge.MOD_ID, getName().toLowerCase().replace(" ", "_")); }
    public String getTooltip() { return Text.translatable("ability."+getID().getPath()+".tooltip").getString(); }
    public abstract int getLV();
    public abstract int getCost();
    public abstract int getCooldown();
    public abstract AbilityType getType();
    public abstract AbilityBase getInstance();
    public void cooldownTick() { this.cooldown -= 1; }

    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.putString("id", getID().getPath());
        nbt.putString("name", getName());
        nbt.putBoolean("active", isActive);
        nbt.putInt("cooldown", cooldown);
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        isActive = nbt.getBoolean("active");
        cooldown = nbt.getInt("cooldown");
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
    public boolean getActive() {
        return isActive;
    }
    public void setCooldownVal(int cooldown) {
        this.cooldown = cooldown;
    }
    public int getCooldownVal() {
        return cooldown;
    }
    public boolean onCooldown() { return cooldown > 0; }

    public boolean cast(ServerPlayerEntity player) {
        setActive(true);
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
    public Optional<CooldownDisplayEntry> getCooldownEntry() {
        return Optional.empty();
    }
}
