package com.pulsar.soulforge.ability;

import net.minecraft.nbt.NbtCompound;

import java.util.Objects;

public abstract class ToggleableAbilityBase extends AbilityBase {
    public final AbilityType type = AbilityType.TOGGLE;
    private boolean active = false;

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean value) {
        this.active = value;
    }

    public void toggleActive() { this.active = !this.active; }

    @Override
    public AbilityType getType() {
        return type;
    }

    @Override
    public NbtCompound saveNbt(NbtCompound nbt) {
        nbt.putBoolean("active", active);
        return super.saveNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!Objects.equals(nbt.getString("id"), getID().getPath())) return;
        active = nbt.getBoolean("active");
    }
}
