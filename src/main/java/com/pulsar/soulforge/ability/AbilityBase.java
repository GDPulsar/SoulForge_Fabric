package com.pulsar.soulforge.ability;

import com.pulsar.soulforge.SoulForge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class AbilityBase {
    private boolean isActive = false;
    private int lastCastTime = 0;
    private int offCooldownTime = 0;

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

    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.putString("id", getID().getPath());
        nbt.putString("name", getName());
        nbt.putBoolean("active", isActive);
        nbt.putInt("lastCastTime", lastCastTime);
        nbt.putInt("offCooldownTime", offCooldownTime);
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        isActive = nbt.getBoolean("active");
        lastCastTime = nbt.getInt("lastCastTime");
        offCooldownTime = nbt.getInt("offCooldownTime");
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
    public boolean getActive() {
        return isActive;
    }
    public void setLastCastTime(int lastCastTime) {
        this.lastCastTime = lastCastTime;
    }
    public int getLastCastTime() {
        return lastCastTime;
    }
    public void setOffCooldownTime(int offCooldownTime) {
        this.offCooldownTime = offCooldownTime;
    }
    public int getOffCooldownTime() {
        return offCooldownTime;
    }

    public boolean cast(ServerPlayerEntity player) {
        setActive(true);
        setLastCastTime(player.getServer().getTicks());
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
